package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.ApiAuthType
import com.maya.assistant.data.model.ApiFormat
import com.maya.assistant.data.model.ConnectionStatus
import com.maya.assistant.data.model.PricingTier

@Entity(tableName = "ai_providers")
data class AiProviderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiKey: String,
    val baseUrl: String,
    val authType: String = "Bearer",
    val apiFormat: String = "OpenAI",
    val selectedModel: String,
    val capabilities: String, // comma-separated capabilities
    val priority: Int = 1,
    val isEnabled: Boolean = true,
    val pricingTier: String = "Unknown",
    val connectionStatus: String = "Untested",
    val lastError: String? = null,
    val isDefault: Boolean = false
) {
    fun toDomain(): AiProvider {
        return AiProvider(
            id = id,
            name = name,
            apiKey = apiKey,
            baseUrl = baseUrl,
            authType = try { ApiAuthType.valueOf(authType) } catch (e: Exception) { ApiAuthType.Bearer },
            apiFormat = try { ApiFormat.valueOf(apiFormat) } catch (e: Exception) { ApiFormat.OpenAI },
            selectedModel = selectedModel,
            capabilities = capabilities.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
            priority = priority,
            isEnabled = isEnabled,
            pricingTier = try { PricingTier.valueOf(pricingTier) } catch (e: Exception) { PricingTier.Unknown },
            connectionStatus = try { ConnectionStatus.valueOf(connectionStatus) } catch (e: Exception) { ConnectionStatus.Untested },
            lastError = lastError,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(p: AiProvider): AiProviderEntity {
            return AiProviderEntity(
                id = p.id,
                name = p.name,
                apiKey = p.apiKey,
                baseUrl = p.baseUrl,
                authType = p.authType.name,
                apiFormat = p.apiFormat.name,
                selectedModel = p.selectedModel,
                capabilities = p.capabilities.joinToString(","),
                priority = p.priority,
                isEnabled = p.isEnabled,
                pricingTier = p.pricingTier.name,
                connectionStatus = p.connectionStatus.name,
                lastError = p.lastError,
                isDefault = p.isDefault
            )
        }
    }
}
