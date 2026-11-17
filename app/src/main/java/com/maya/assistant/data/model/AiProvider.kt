package com.maya.assistant.data.model

data class AiProvider(
    val id: Long = 0,
    val name: String,
    val apiKey: String = "",
    val baseUrl: String,
    val authType: ApiAuthType = ApiAuthType.Bearer,
    val apiFormat: ApiFormat = ApiFormat.OpenAI,
    val selectedModel: String,
    val capabilities: Set<String> = setOf("conversation"),
    val priority: Int = 1,
    val isEnabled: Boolean = true,
    val pricingTier: PricingTier = PricingTier.Unknown,
    val connectionStatus: ConnectionStatus = ConnectionStatus.Untested,
    val lastError: String? = null,
    val isDefault: Boolean = false
) {
    val maskedApiKey: String
        get() = when {
            apiKey.isBlank() -> "No Key"
            apiKey.length <= 8 -> "••••••••"
            else -> "${apiKey.take(4)}••••••••${apiKey.takeLast(4)}"
        }

    val isFree: Boolean
        get() = pricingTier == PricingTier.VerifiedFree || pricingTier == PricingTier.FreeTier
}

enum class ApiAuthType {
    Bearer,
    ApiKeyHeader,
    QueryParam,
    None
}

enum class ApiFormat {
    Gemini,
    OpenAI,
    Anthropic,
    Custom
}

enum class ConnectionStatus {
    Untested,
    Testing,
    Connected,
    Failed
}

enum class PricingTier(val displayName: String) {
    VerifiedFree("Verified Free"),
    FreeTier("Free Tier"),
    Trial("Trial"),
    Paid("Paid"),
    Unknown("Unknown"),
    Unavailable("Unavailable")
}

data class DiscoveredModel(
    val id: String,
    val name: String,
    val contextLength: Int = 4096,
    val capabilities: Set<String> = emptySet(),
    val pricingTier: PricingTier = PricingTier.Unknown,
    val isAvailable: Boolean = true
)
