package com.maya.assistant.ui.screens.providers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.ApiAuthType
import com.maya.assistant.data.model.ApiFormat
import com.maya.assistant.data.model.DiscoveredModel
import com.maya.assistant.data.model.PricingTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProvidersViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val repository = mayaApp.aiProviderRepository

    val providers: StateFlow<List<AiProvider>> = repository.getAllProviders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _testingProviderId = MutableStateFlow<Long?>(null)
    val testingProviderId: StateFlow<Long?> = _testingProviderId.asStateFlow()

    private val _connectionResult = MutableStateFlow<Pair<Long, String>?>(null)
    val connectionResult: StateFlow<Pair<Long, String>?> = _connectionResult.asStateFlow()

    private val _discoveredModels = MutableStateFlow<List<DiscoveredModel>>(emptyList())
    val discoveredModels: StateFlow<List<DiscoveredModel>> = _discoveredModels.asStateFlow()

    fun testConnection(provider: AiProvider) {
        viewModelScope.launch {
            _testingProviderId.value = provider.id
            val result = repository.testConnection(provider)
            _testingProviderId.value = null
            _connectionResult.value = Pair(provider.id, result.message)
        }
    }

    fun discoverModels(provider: AiProvider) {
        viewModelScope.launch {
            val models = repository.discoverModels(provider)
            _discoveredModels.value = models
        }
    }

    fun toggleProvider(provider: AiProvider) {
        viewModelScope.launch {
            repository.updateProvider(provider.copy(isEnabled = !provider.isEnabled))
        }
    }

    fun setDefaultProvider(id: Long) {
        viewModelScope.launch {
            repository.setDefault(id)
        }
    }

    fun addCustomProvider(
        name: String,
        apiKey: String,
        baseUrl: String,
        modelName: String,
        pricingTier: PricingTier = PricingTier.Unknown
    ) {
        viewModelScope.launch {
            val newProvider = AiProvider(
                name = name,
                apiKey = apiKey,
                baseUrl = baseUrl,
                selectedModel = modelName,
                capabilities = setOf("conversation", "reasoning", "coding"),
                pricingTier = pricingTier,
                apiFormat = ApiFormat.OpenAI,
                authType = ApiAuthType.Bearer,
                isEnabled = true
            )
            repository.addProvider(newProvider)
        }
    }

    fun deleteProvider(id: Long) {
        viewModelScope.launch {
            repository.deleteProvider(id)
        }
    }
}
