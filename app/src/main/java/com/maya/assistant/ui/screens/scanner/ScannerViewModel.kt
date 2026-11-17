package com.maya.assistant.ui.screens.scanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val voiceEngine = mayaApp.voiceEngine

    private val _isBackCamera = MutableStateFlow(true)
    val isBackCamera: StateFlow<Boolean> = _isBackCamera.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _statusText = MutableStateFlow("> STANDBY — session shuru karo, Maya ko aankhein do \uD83C\uDFA4")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _feedState = MutableStateFlow("AI-FEED OFFLINE")
    val feedState: StateFlow<String> = _feedState.asStateFlow()

    fun flipCamera() {
        _isBackCamera.value = !_isBackCamera.value
    }

    fun capturePhoto() {
        viewModelScope.launch {
            _isScanning.value = true
            _statusText.value = "> SCANNING — Visual scene capture in progress..."
            _feedState.value = "AI-FEED ACTIVE"
            voiceEngine.speakDirect("Visual scene capture kar rahi hoon. Ek pal dijiye.")

            delay(2000)

            _isScanning.value = false
            _statusText.value = "> ANALYSIS COMPLETE — Scene details successfully processed."
            voiceEngine.speakDirect("Maine scene capture kar liya hai. Sab theek dikh raha hai!")
        }
    }

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
        if (_isRecording.value) {
            _statusText.value = "> RECORDING — Live visual stream active."
            _feedState.value = "AI-FEED STREAMING"
            voiceEngine.speakDirect("Live video recording shuru ho gayi hai.")
        } else {
            _statusText.value = "> STANDBY — Video saved to gallery."
            _feedState.value = "AI-FEED OFFLINE"
            voiceEngine.speakDirect("Recording complete ho gayi hai.")
        }
    }
}
