package com.maya.assistant.ai

import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.PricingTier
import com.maya.assistant.data.repository.AiProviderRepository
import kotlinx.coroutines.delay
import java.io.IOException

class MultiModelOrchestrator(
    private val providerRepository: AiProviderRepository
) {

    private val circuitBreakers = mutableMapOf<Long, Int>() // providerId -> failure count

    suspend fun routeAndExecute(
        capability: String,
        prompt: String,
        isFreeModeOnly: Boolean = true,
        contextData: String = "",
        onFallbackTriggered: ((fromProvider: String, toProvider: String, reason: String) -> Unit)? = null
    ): ExecutionResult {
        val eligibleProviders = getEligibleProviders(capability, isFreeModeOnly)

        if (eligibleProviders.isEmpty()) {
            return ExecutionResult.Failure(
                error = "No active provider available for capability: $capability (Free-mode-only: $isFreeModeOnly)",
                isFallbackEligible = false
            )
        }

        var lastError: String = ""
        for (i in eligibleProviders.indices) {
            val provider = eligibleProviders[i]

            // Check Circuit Breaker
            val failures = circuitBreakers[provider.id] ?: 0
            if (failures >= 3) {
                continue // Skip tripped circuit breaker
            }

            // Attempt execution with exponential backoff
            val result = executeWithRetry(provider, prompt, contextData)

            if (result is ExecutionResult.Success) {
                // Reset circuit breaker on success
                circuitBreakers[provider.id] = 0
                return result
            } else if (result is ExecutionResult.Failure) {
                lastError = result.error

                // Check fallback eligibility
                if (!result.isFallbackEligible) {
                    // Do not fallback on auth error or malformed request!
                    return result
                }

                // Increment circuit breaker count
                circuitBreakers[provider.id] = failures + 1

                // Fallback to next provider if available
                if (i + 1 < eligibleProviders.size) {
                    val nextProvider = eligibleProviders[i + 1]
                    onFallbackTriggered?.invoke(provider.name, nextProvider.name, result.error)
                }
            }
        }

        return ExecutionResult.Failure(
            error = "All eligible providers failed. Last error: $lastError",
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
        maxRetries: Int = 2
    ): ExecutionResult {
        var backoffMs = 500L
        for (attempt in 0..maxRetries) {
            val res = simulateOrCallProvider(provider, prompt, contextData)
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

    private fun simulateOrCallProvider(
        provider: AiProvider,
        prompt: String,
        contextData: String
    ): ExecutionResult {
        // Evaluate simulated or actual network state
        if (provider.apiKey.isBlank() && provider.authType != com.maya.assistant.data.model.ApiAuthType.None) {
            return ExecutionResult.Failure(
                error = "Authentication failed: Missing API key for ${provider.name} (HTTP 401)",
                isFallbackEligible = false // Do not fallback on auth error!
            )
        }

        // Return structured AI generation
        val combinedPrompt = if (contextData.isNotBlank()) "$contextData\n\nUser: $prompt" else prompt
        return ExecutionResult.Success(
            content = "[$provider.name | ${provider.selectedModel}]: Response to \"$prompt\"",
            providerUsed = provider.name,
            modelUsed = provider.selectedModel,
            isFree = provider.isFree
        )
    }

    fun isErrorFallbackEligible(httpCode: Int): Boolean {
        return when (httpCode) {
            429 -> true  // Rate limit / Quota exhaustion
            500, 502, 503, 504 -> true // Temporary outage
            401, 403 -> false // Auth or permission failure
            400, 422 -> false // Malformed request
            else -> false
        }
    }

    suspend fun generateVisionResponse(
        prompt: String,
        imageBase64: String,
        isFreeModeOnly: Boolean = true
    ): String {
        val result = routeAndExecute(
            capability = "vision",
            prompt = prompt,
            isFreeModeOnly = isFreeModeOnly,
            contextData = "[Image Data Base64: ${imageBase64.take(40)}...]"
        )
        return when (result) {
            is ExecutionResult.Success -> result.content
            is ExecutionResult.Failure -> "Vision response unavailable: ${result.error}"
        }
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
