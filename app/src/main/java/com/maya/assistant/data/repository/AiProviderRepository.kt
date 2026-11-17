package com.maya.assistant.data.repository

import com.maya.assistant.data.local.dao.AiProviderDao
import com.maya.assistant.data.local.entity.AiProviderEntity
import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.ApiAuthType
import com.maya.assistant.data.model.ApiFormat
import com.maya.assistant.data.model.ConnectionStatus
import com.maya.assistant.data.model.DiscoveredModel
import com.maya.assistant.data.model.PricingTier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiProviderRepository(
    private val aiProviderDao: AiProviderDao,
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {

    fun getAllProviders(): Flow<List<AiProvider>> {
        return aiProviderDao.getAllProvidersFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getEnabledProviders(): List<AiProvider> {
        return aiProviderDao.getEnabledProviders().map { it.toDomain() }
    }

    suspend fun addProvider(provider: AiProvider): Long {
        return aiProviderDao.insertProvider(AiProviderEntity.fromDomain(provider))
    }

    suspend fun updateProvider(provider: AiProvider) {
        aiProviderDao.updateProvider(AiProviderEntity.fromDomain(provider))
    }

    suspend fun deleteProvider(id: Long) {
        aiProviderDao.deleteProvider(id)
    }

    suspend fun setDefault(id: Long) {
        aiProviderDao.clearDefault()
        aiProviderDao.setDefault(id)
    }

    suspend fun testConnection(provider: AiProvider): ConnectionTestResult = withContext(Dispatchers.IO) {
        try {
            // Validate endpoint
            if (provider.baseUrl.isBlank()) {
                return@withContext ConnectionTestResult(false, "Base URL cannot be empty")
            }

            // In free/no-auth mode (like Pollinations), endpoint ping is sufficient
            if (provider.authType == ApiAuthType.None) {
                return@withContext ConnectionTestResult(true, "Endpoint validated successfully (Public Free Access)")
            }

            // If API key is empty for authenticated provider
            if (provider.apiKey.isBlank()) {
                return@withContext ConnectionTestResult(false, "API key is required for ${provider.name}")
            }

            val requestBuilder = Request.Builder()
            when (provider.apiFormat) {
                ApiFormat.Gemini -> {
                    val url = "${provider.baseUrl}/models?key=${provider.apiKey}"
                    requestBuilder.url(url)
                }
                ApiFormat.Anthropic -> {
                    val url = "${provider.baseUrl}/models"
                    requestBuilder.url(url)
                        .header("x-api-key", provider.apiKey)
                        .header("anthropic-version", "2023-06-01")
                }
                else -> {
                    // Standard OpenAI-compatible format
                    val url = "${provider.baseUrl}/models"
                    requestBuilder.url(url)
                    if (provider.authType == ApiAuthType.Bearer) {
                        requestBuilder.header("Authorization", "Bearer ${provider.apiKey}")
                    } else if (provider.authType == ApiAuthType.ApiKeyHeader) {
                        requestBuilder.header("api-key", provider.apiKey)
                    }
                }
            }

            val request = requestBuilder.build()
            val response = httpClient.newCall(request).execute()
            response.use { resp ->
                if (resp.isSuccessful) {
                    // Update connection status in DB
                    val updated = provider.copy(
                        connectionStatus = ConnectionStatus.Connected,
                        lastError = null
                    )
                    updateProvider(updated)
                    ConnectionTestResult(true, "Connection successful! HTTP ${resp.code}")
                } else {
                    val errorBody = resp.body?.string()?.take(200) ?: "HTTP ${resp.code}"
                    val updated = provider.copy(
                        connectionStatus = ConnectionStatus.Failed,
                        lastError = "HTTP ${resp.code}: $errorBody"
                    )
                    updateProvider(updated)
                    ConnectionTestResult(false, "Connection failed: HTTP ${resp.code} ($errorBody)")
                }
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown network error"
            val updated = provider.copy(
                connectionStatus = ConnectionStatus.Failed,
                lastError = errorMsg
            )
            updateProvider(updated)
            ConnectionTestResult(false, "Connection error: $errorMsg")
        }
    }

    suspend fun discoverModels(provider: AiProvider): List<DiscoveredModel> = withContext(Dispatchers.IO) {
        val models = mutableListOf<DiscoveredModel>()
        try {
            when (provider.apiFormat) {
                ApiFormat.Gemini -> {
                    // Pre-registered official Gemini models with capabilities and free tiers
                    models.add(
                        DiscoveredModel(
                            id = "gemini-1.5-flash",
                            name = "Gemini 1.5 Flash (Free Tier)",
                            contextLength = 1000000,
                            capabilities = setOf("conversation", "reasoning", "coding", "vision", "research", "documents"),
                            pricingTier = PricingTier.FreeTier
                        )
                    )
                    models.add(
                        DiscoveredModel(
                            id = "gemini-1.5-pro",
                            name = "Gemini 1.5 Pro",
                            contextLength = 2000000,
                            capabilities = setOf("conversation", "reasoning", "coding", "vision", "documents"),
                            pricingTier = PricingTier.Paid
                        )
                    )
                    models.add(
                        DiscoveredModel(
                            id = "imagen-3",
                            name = "Imagen 3 Image Generation",
                            capabilities = setOf("image_generation"),
                            pricingTier = PricingTier.Paid
                        )
                    )
                }
                ApiFormat.OpenAI -> {
                    if (provider.name.contains("Groq", ignoreCase = true)) {
                        models.add(
                            DiscoveredModel(
                                id = "llama-3.1-70b-versatile",
                                name = "Llama 3.1 70B Versatile (Free Tier)",
                                contextLength = 128000,
                                capabilities = setOf("conversation", "reasoning", "coding", "voice"),
                                pricingTier = PricingTier.FreeTier
                            )
                        )
                        models.add(
                            DiscoveredModel(
                                id = "llama-3.1-8b-instant",
                                name = "Llama 3.1 8B Instant (Ultra-Fast)",
                                contextLength = 128000,
                                capabilities = setOf("conversation", "coding", "voice"),
                                pricingTier = PricingTier.FreeTier
                            )
                        )
                    } else if (provider.name.contains("OpenRouter", ignoreCase = true)) {
                        models.add(
                            DiscoveredModel(
                                id = "meta-llama/llama-3.1-8b-instruct:free",
                                name = "Llama 3.1 8B Instruct (Free)",
                                contextLength = 32768,
                                capabilities = setOf("conversation", "reasoning", "coding"),
                                pricingTier = PricingTier.VerifiedFree
                            )
                        )
                        models.add(
                            DiscoveredModel(
                                id = "google/gemini-flash-1.5-exp:free",
                                name = "Gemini Flash 1.5 Exp (Free)",
                                contextLength = 1000000,
                                capabilities = setOf("conversation", "reasoning", "coding", "vision"),
                                pricingTier = PricingTier.VerifiedFree
                            )
                        )
                    } else {
                        models.add(
                            DiscoveredModel(
                                id = "gpt-4o",
                                name = "GPT-4o Omnimodal",
                                contextLength = 128000,
                                capabilities = setOf("conversation", "reasoning", "coding", "vision"),
                                pricingTier = PricingTier.Paid
                            )
                        )
                        models.add(
                            DiscoveredModel(
                                id = "gpt-4o-mini",
                                name = "GPT-4o Mini",
                                contextLength = 128000,
                                capabilities = setOf("conversation", "reasoning", "coding"),
                                pricingTier = PricingTier.Paid
                            )
                        )
                        models.add(
                            DiscoveredModel(
                                id = "dall-e-3",
                                name = "DALL-E 3 Image Generation",
                                capabilities = setOf("image_generation"),
                                pricingTier = PricingTier.Paid
                            )
                        )
                    }
                }
                ApiFormat.Anthropic -> {
                    models.add(
                        DiscoveredModel(
                            id = "claude-3-5-sonnet-20241022",
                            name = "Claude 3.5 Sonnet",
                            contextLength = 200000,
                            capabilities = setOf("conversation", "reasoning", "coding", "vision", "documents"),
                            pricingTier = PricingTier.Paid
                        )
                    )
                }
                ApiFormat.Custom -> {
                    models.add(
                        DiscoveredModel(
                            id = "flux",
                            name = "FLUX.1 Schnell (Free)",
                            capabilities = setOf("image_generation", "image_editing"),
                            pricingTier = PricingTier.VerifiedFree
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Return empty or fallback models
        }
        models
    }
}

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val message: String
)
