package com.maya.assistant.ui.screens.image

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.ai.image.GeneratedArt
import com.maya.assistant.ai.image.ImageEditOperation
import com.maya.assistant.data.local.entity.GeneratedImageEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImageStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val imageEngine = mayaApp.imageStudioEngine
    private val dao = mayaApp.database.generatedImageDao()

    val history: StateFlow<List<GeneratedImageEntity>> = dao.getAllImagesFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentPrompt = MutableStateFlow("")
    val currentPrompt: StateFlow<String> = _currentPrompt.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow("1:1")
    val selectedAspectRatio: StateFlow<String> = _selectedAspectRatio.asStateFlow()

    private val _selectedStyle = MutableStateFlow("Cinematic")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentArt = MutableStateFlow<GeneratedArt?>(null)
    val currentArt: StateFlow<GeneratedArt?> = _currentArt.asStateFlow()

    fun updatePrompt(text: String) {
        _currentPrompt.value = text
    }

    fun setAspectRatio(ratio: String) {
        _selectedAspectRatio.value = ratio
    }

    fun setStyle(style: String) {
        _selectedStyle.value = style
    }

    fun enhancePrompt() {
        if (_currentPrompt.value.isNotBlank()) {
            _currentPrompt.value = imageEngine.enhancePrompt(_currentPrompt.value, _selectedStyle.value)
        }
    }

    fun generateImage() {
        val prompt = _currentPrompt.value.trim()
        if (prompt.isBlank() || _isGenerating.value) return

        _isGenerating.value = true
        viewModelScope.launch {
            delay(1500) // Simulated creative rendering
            val art = imageEngine.generateImageUrl(
                prompt = prompt,
                aspectRatio = _selectedAspectRatio.value,
                style = _selectedStyle.value
            )
            _currentArt.value = art
            _isGenerating.value = false

            // Save to database
            dao.insertImage(
                GeneratedImageEntity(
                    prompt = prompt,
                    imageUrl = art.imageUrl,
                    aspectRatio = art.aspectRatio,
                    providerUsed = "Pollinations FLUX (Verified Free)"
                )
            )
        }
    }

    fun applyEdit(operation: ImageEditOperation) {
        val art = _currentArt.value ?: return
        val editedUrl = imageEngine.applyEditOperation(art.imageUrl, operation)
        _currentArt.value = art.copy(imageUrl = editedUrl)
    }
}
