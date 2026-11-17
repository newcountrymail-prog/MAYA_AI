package com.maya.assistant.ui.screens.scanner

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.vision.VisionTaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val voiceEngine = mayaApp.voiceEngine
    private val screenVisionManager = mayaApp.screenVisionManager

    private val _isBackCamera = MutableStateFlow(true)
    val isBackCamera: StateFlow<Boolean> = _isBackCamera.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private val _statusText = MutableStateFlow("Camera ready — Maya ko dikhaiye aur poochhiye!")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _latestSolution = MutableStateFlow<String?>(null)
    val latestSolution: StateFlow<String?> = _latestSolution.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    fun flipCamera() {
        _isBackCamera.value = !_isBackCamera.value
    }

    fun toggleTorch() {
        _isTorchOn.value = !_isTorchOn.value
    }

    fun analyzeCapturedFrame(bitmap: Bitmap, userQuery: String? = null) {
        viewModelScope.launch {
            _capturedBitmap.value = bitmap
            _isScanning.value = true
            _statusText.value = "Maya photo analyze kar rahi hai..."
            voiceEngine.speakDirect("Photo dekh rahi hoon, ek second...")

            val queryPrompt = if (!userQuery.isNullOrBlank()) {
                userQuery
            } else {
                "Is photo ko dhyan se dekho aur explain karo ki kya hai, koi problem ya error ho toh Hinglish me solution batao."
            }

            val result = screenVisionManager.analyzeBitmap(
                bitmap = bitmap,
                taskType = VisionTaskType.OBJECT_DESCRIPTION,
                userQuery = queryPrompt
            )

            _isScanning.value = false
            if (result.success && result.summary.isNotBlank()) {
                _latestSolution.value = result.summary
                _statusText.value = "Analysis complete!"
                // Speak out the real answer in Hinglish
                voiceEngine.speakDirect(result.summary)
            } else {
                val errorMsg = "Photo clear nahi aayi ya network issue hai. Kripya dobara try kijiye."
                _statusText.value = errorMsg
                voiceEngine.speakDirect(errorMsg)
            }
        }
    }

    fun clearSolution() {
        _latestSolution.value = null
        _statusText.value = "Camera ready — Maya ko dikhaiye aur poochhiye!"
    }
}
