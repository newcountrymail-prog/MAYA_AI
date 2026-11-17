package com.maya.assistant.data.model

data class ChatMessage(
    val id: Long = 0,
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = false,
    val attachmentUri: String? = null,
    val attachmentType: String? = null
)

enum class MessageSender {
    USER,
    MAYA,
    SYSTEM
}
