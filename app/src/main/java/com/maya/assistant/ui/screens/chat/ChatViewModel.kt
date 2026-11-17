package com.maya.assistant.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.ChatMessage
import com.maya.assistant.data.model.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val chatRepository = mayaApp.chatRepository
    private val voiceEngine = mayaApp.voiceEngine

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getAllMessages().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isGenerating.value) return

        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            chatRepository.sendMessage(
                ChatMessage(
                    sender = MessageSender.USER,
                    content = text
                )
            )

            voiceEngine.processQuery(text) { responseText ->
                viewModelScope.launch {
                    chatRepository.sendMessage(
                        ChatMessage(
                            sender = MessageSender.MAYA,
                            content = responseText
                        )
                    )
                    _isGenerating.value = false
                }
            }
        }
    }

    fun replayMayaVoice(text: String) {
        voiceEngine.speakDirect(text)
    }

    fun clearAllChat() {
        viewModelScope.launch {
            chatRepository.clearChat()
        }
    }
}
