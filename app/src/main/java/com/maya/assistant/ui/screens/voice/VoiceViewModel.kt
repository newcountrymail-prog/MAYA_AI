package com.maya.assistant.ui.screens.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.data.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val voiceEngine = mayaApp.voiceEngine
    private val preferencesManager = mayaApp.preferencesManager

    val orbState: StateFlow<OrbState> = voiceEngine.orbState
    val rmsAmplitude: StateFlow<Float> = voiceEngine.rmsAmplitude
    val transcription: StateFlow<String> = voiceEngine.transcription
    val lastResponse: StateFlow<String> = voiceEngine.lastResponse

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    fun startListening() {
        voiceEngine.startListening()
    }

    fun stopListening() {
        voiceEngine.stopListening()
    }

    fun interrupt() {
        voiceEngine.interrupt()
    }

    fun toggleVoiceLanguage() {
        viewModelScope.launch {
            val current = userSettings.value.voiceLanguage
            val next = if (current == "hi-IN") "en-IN" else "hi-IN"
            preferencesManager.updateVoiceLanguage(next)
            voiceEngine.speakDirect(
                if (next == "hi-IN") "Hindi bhasha activate ho gayi hai."
                else "English voice mode activated."
            )
        }
    }
}
