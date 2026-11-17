package com.maya.assistant.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val preferencesManager = mayaApp.preferencesManager
    private val voiceEngine = mayaApp.voiceEngine

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    fun updateUserName(name: String) {
        viewModelScope.launch {
            preferencesManager.updateUserName(name)
        }
    }

    fun updateVoiceLanguage(lang: String) {
        viewModelScope.launch {
            preferencesManager.updateVoiceLanguage(lang)
            voiceEngine.speakDirect(
                if (lang.startsWith("hi")) "Awaaz Hindi bhasha me set kar di gayi hai."
                else "Voice language updated to Indian English."
            )
        }
    }

    fun updateVoiceSpeed(speed: Float) {
        viewModelScope.launch {
            preferencesManager.updateVoiceSpeed(speed)
        }
    }

    fun updateVoicePitch(pitch: Float) {
        viewModelScope.launch {
            preferencesManager.updateVoicePitch(pitch)
        }
    }

    fun updateVoiceEngineMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.updateVoiceEngineMode(mode)
        }
    }

    fun toggleGirlfriendMode() {
        val current = userSettings.value.isGirlfriendMode
        viewModelScope.launch {
            preferencesManager.updateGirlfriendMode(!current)
            if (!current) {
                voiceEngine.speakDirect("Girlfriend mode activate ho gaya hai! Main aapka hamesha dhyan rakhungi.")
            } else {
                voiceEngine.speakDirect("Standard assistant mode wapas set ho gaya hai.")
            }
        }
    }

    fun toggleTheme() {
        val current = userSettings.value.isDarkTheme
        viewModelScope.launch {
            preferencesManager.updateTheme(!current)
        }
    }
}
