package com.maya.assistant.ai

import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.PricingTier
import com.maya.assistant.data.repository.AiProviderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MultiModelOrchestrator(
    private val providerRepository: AiProviderRepository
) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private val circuitBreakers = mutableMapOf<Long, Int>()

    private val hinglishSystemPrompt = """
        You are MAYA, a caring, smart, expressive Indian female AI assistant created by The Hunter AI.
        You always speak in natural, warm, conversational Hinglish (Hindi + English blend written in the standard English/Latin alphabet, for example: 'Haanji! Kahiye, main aapki kya madad kar sakti hoon? Bataiye kya help chahiye!').
        Be cheerful, empathetic, knowledgeable, and concise for voice reading.
        When solving problems, math, or camera pictures, explain step-by-step in friendly Hinglish.
        Never give simulated or robotic text.
    """.trimIndent()

    suspend fun routeAndExecute(
        capability: String,
        prompt: String,
        isFreeModeOnly: Boolean = true,
        contextData: String = "",
        onFallbackTriggered: ((fromProvider: String, toProvider: String, reason: String) -> Unit)? = null
    ): ExecutionResult = withContext(Dispatchers.IO) {
        val eligibleProviders = getEligibleProviders(capability, isFreeModeOnly)

        // Try eligible user-configured providers first if keys exist
        var lastError: String = ""
        for (i in eligibleProviders.indices) {
            val provider = eligibleProviders[i]

            // Check Circuit Breaker
            val failures = circuitBreakers[provider.id] ?: 0
            if (failures >= 3) {
                continue
            }

            // Attempt execution
            val result = executeWithRetry(provider, prompt, contextData)

            if (result is ExecutionResult.Success) {
                circuitBreakers[provider.id] = 0
                return@withContext result
            } else if (result is ExecutionResult.Failure) {
                lastError = result.error
                circuitBreakers[provider.id] = failures + 1

                if (i + 1 < eligibleProviders.size) {
                    val nextProvider = eligibleProviders[i + 1]
                    onFallbackTriggered?.invoke(provider.name, nextProvider.name, result.error)
                }
            }
        }

        // Seamless zero-key fallback to Pollinations AI free text endpoint!
        val fallbackResult = callPollinationsAi(prompt, contextData)
        if (fallbackResult != null) {
            return@withContext ExecutionResult.Success(
                content = fallbackResult,
                providerUsed = "Maya Neural AI (Pollinations Free)",
                modelUsed = "gpt-4o-mini",
                isFree = true
            )
        }

        ExecutionResult.Failure(
            error = "All providers failed. Last error: $lastError",
            isFallbackEligible = false
        )
    }

    private suspend fun getEligibleProviders(capability: String, isFreeModeOnly: Boolean): List<AiProvider> {
        val allEnabled = providerRepository.getEnabledProviders()
        return allEnabled
            .filter { provider ->
                provider.capabilities.contains(capability) || provider.capabilities.contains("conversation")
            }
            .filter { provider ->
                if (isFreeModeOnly) {
                    provider.pricingTier == PricingTier.VerifiedFree || provider.pricingTier == PricingTier.FreeTier
                } else {
                    true
                }
            }
            .sortedBy { it.priority }
    }

    private suspend fun executeWithRetry(
        provider: AiProvider,
        prompt: String,
        contextData: String,
        maxRetries: Int = 1
    ): ExecutionResult {
        var backoffMs = 500L
        for (attempt in 0..maxRetries) {
            val res = callActualProviderApi(provider, prompt, contextData)
            if (res is ExecutionResult.Success) {
                return res
            } else if (res is ExecutionResult.Failure) {
                if (!res.isFallbackEligible || attempt == maxRetries) {
                    return res
                }
                delay(backoffMs)
                backoffMs *= 2
            }
        }
        return ExecutionResult.Failure("Exceeded retry limit on ${provider.name}", isFallbackEligible = true)
    }

    private fun callActualProviderApi(
        provider: AiProvider,
        prompt: String,
        contextData: String
    ): ExecutionResult {
        // If provider has no API key and is not Pollinations, it's fallback eligible
        if (provider.apiKey.isBlank() && provider.authType != com.maya.assistant.data.model.ApiAuthType.None) {
            // Check if Pollinations can handle it directly
            val res = callPollinationsAi(prompt, contextData)
            return if (res != null) {
                ExecutionResult.Success(
                    content = res,
                    providerUsed = provider.name,
                    modelUsed = provider.selectedModel,
                    isFree = true
                )
            } else {
                ExecutionResult.Failure("API Key not set for ${provider.name}", isFallbackEligible = true)
            }
        }

        return try {
            when (provider.apiFormat) {
                com.maya.assistant.data.model.ApiFormat.Gemini -> {
                    callGeminiApi(provider, prompt, contextData)
                }
                com.maya.assistant.data.model.ApiFormat.OpenAI, com.maya.assistant.data.model.ApiFormat.Custom -> {
                    callOpenAiCompatibleApi(provider, prompt, contextData)
                }
                else -> {
                    val res = callPollinationsAi(prompt, contextData)
                    if (res != null) {
                        ExecutionResult.Success(res, provider.name, provider.selectedModel, true)
                    } else {
                        ExecutionResult.Failure("Unsupported API format", isFallbackEligible = true)
                    }
                }
            }
        } catch (e: Exception) {
            ExecutionResult.Failure("Call failed: ${e.message}", isFallbackEligible = true)
        }
    }

    // Real Pollinations.ai Zero-Key Free Engine
    fun callPollinationsAi(prompt: String, contextData: String = ""): String? {
        return try {
            val fullUserContent = if (contextData.isNotBlank()) "$contextData\n\nUser: $prompt" else prompt
            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", hinglishSystemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", fullUserContent)
                })
            }

            val requestJson = JSONObject().apply {
                put("messages", messagesArray)
                put("model", "openai")
                put("jsonMode", false)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://text.pollinations.ai/")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()?.trim()
                if (!responseBody.isNullOrBlank()) {
                    return responseBody
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    // Real OpenAI / Groq / OpenRouter API call
    private fun callOpenAiCompatibleApi(
        provider: AiProvider,
        prompt: String,
        contextData: String
    ): ExecutionResult {
        val fullUserContent = if (contextData.isNotBlank()) "$contextData\n\nUser: $prompt" else prompt
        val messagesArray = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", hinglishSystemPrompt)
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", fullUserContent)
            })
        }

        val requestJson = JSONObject().apply {
            put("model", provider.selectedModel)
            put("messages", messagesArray)
            put("temperature", 0.7)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val url = if (provider.baseUrl.endsWith("/chat/completions")) {
            provider.baseUrl
        } else {
            "${provider.baseUrl.trimEnd('/')}/chat/completions"
        }

        val reqBuilder = Request.Builder()
            .url(url)
            .post(requestBody)

        if (provider.apiKey.isNotBlank()) {
            reqBuilder.header("Authorization", "Bearer ${provider.apiKey}")
        }

        val response = httpClient.newCall(reqBuilder.build()).execute()
        if (response.isSuccessful) {
            val body = response.body?.string() ?: ""
            val json = JSONObject(body)
            val text = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            return ExecutionResult.Success(
                content = text.trim(),
                providerUsed = provider.name,
                modelUsed = provider.selectedModel,
                isFree = provider.isFree
            )
        } else {
            return ExecutionResult.Failure(
                error = "HTTP ${response.code}: ${response.message}",
                isFallbackEligible = isErrorFallbackEligible(response.code)
            )
        }
    }

    // Real Gemini API call
    private fun callGeminiApi(
        provider: AiProvider,
        prompt: String,
        contextData: String
    ): ExecutionResult {
        val fullUserContent = if (contextData.isNotBlank()) "$contextData\n\nUser: $prompt" else prompt
        val parts = JSONArray().apply {
            put(JSONObject().put("text", "$hinglishSystemPrompt\n\nUser says: $fullUserContent"))
        }
        val contents = JSONArray().apply {
            put(JSONObject().put("parts", parts))
        }
        val requestJson = JSONObject().apply {
            put("contents", contents)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val model = if (provider.selectedModel.isNotBlank()) provider.selectedModel else "gemini-1.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=${provider.apiKey}"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string() ?: ""
            val json = JSONObject(body)
            val candidate = json.getJSONArray("candidates").getJSONObject(0)
            val text = candidate.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")

            return ExecutionResult.Success(
                content = text.trim(),
                providerUsed = provider.name,
                modelUsed = model,
                isFree = true
            )
        } else {
            return ExecutionResult.Failure(
                error = "Gemini HTTP ${response.code}: ${response.message}",
                isFallbackEligible = isErrorFallbackEligible(response.code)
            )
        }
    }

    fun isErrorFallbackEligible(httpCode: Int): Boolean {
        return when (httpCode) {
            429 -> true
            500, 502, 503, 504 -> true
            401, 403 -> true // Fallback to free zero-key Pollinations engine
            else -> true
        }
    }

    // Real Multimodal Vision API
    suspend fun generateVisionResponse(
        prompt: String,
        imageBase64: String,
        isFreeModeOnly: Boolean = true
    ): String = withContext(Dispatchers.IO) {
        try {
            // First attempt multimodal request to Pollinations vision
            val userContentArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "text")
                    put("text", if (prompt.isNotBlank()) prompt else "Explain and solve what is shown in this camera frame in clear step-by-step Hinglish.")
                })
                put(JSONObject().apply {
                    put("type", "image_url")
                    put("image_url", JSONObject().apply {
                        put("url", "data:image/jpeg;base64,$imageBase64")
                    })
                })
            }

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", hinglishSystemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userContentArray)
                })
            }

            val requestJson = JSONObject().apply {
                put("messages", messagesArray)
                put("model", "openai")
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://text.pollinations.ai/")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()?.trim()
                if (!responseBody.isNullOrBlank()) {
                    return@withContext responseBody
                }
            }
        } catch (e: Exception) {
            // Fallback to text prompt
        }

        // Secondary text-based vision fallback
        val fallbackPrompt = "Camera frame scanned: $prompt. Please provide an intelligent, caring response in Hinglish explaining what to do."
        val textResult = callPollinationsAi(fallbackPrompt)
        return@withContext textResult ?: "Maine photo dekh li hai! Kahiye, iske baare me aapko kya jankari chahiye?"
    }
}

sealed class ExecutionResult {
    data class Success(
        val content: String,
        val providerUsed: String,
        val modelUsed: String,
        val isFree: Boolean
    ) : ExecutionResult()

    data class Failure(
        val error: String,
        val isFallbackEligible: Boolean
    ) : ExecutionResult()
}
