package com.maya.assistant.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.ChatMessage
import com.maya.assistant.data.model.MessageSender
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val voiceEngine = mayaApp.voiceEngine
    private val chatRepository = mayaApp.chatRepository
    private val preferencesManager = mayaApp.preferencesManager

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    val orbState: StateFlow<OrbState> = voiceEngine.orbState
    val rmsAmplitude: StateFlow<Float> = voiceEngine.rmsAmplitude

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun onSendClicked() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        _inputText.value = ""
        viewModelScope.launch {
            // Save User Message
            chatRepository.sendMessage(
                ChatMessage(
                    sender = MessageSender.USER,
                    content = text
                )
            )

            // Voice/Persona responds
            voiceEngine.processQuery(text) { responseText ->
                viewModelScope.launch {
                    chatRepository.sendMessage(
                        ChatMessage(
                            sender = MessageSender.MAYA,
                            content = responseText
                        )
                    )
                }
            }
        }
    }

    fun toggleVoiceListening() {
        if (orbState.value == OrbState.Listening || orbState.value == OrbState.Speaking) {
            voiceEngine.interrupt()
        } else {
            voiceEngine.startListening()
        }
    }

    fun onQuickAction(action: String) {
        val query = when (action) {
            "gaane" -> "Gaane bajao aur relaxation music shuru karo"
            "padhai" -> "Padhai me madad karo, summary do"
            "journal" -> "Journal note likhna hai"
            else -> action
        }
        _inputText.value = query
        onSendClicked()
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun getFormattedDate(): Pair<String, String> {
        val dayNumber = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        val dayName = SimpleDateFormat("EEE, MMM", Locale.getDefault()).format(Date())
        return Pair(dayNumber, dayName)
    }
}
