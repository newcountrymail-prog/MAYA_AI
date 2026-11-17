package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maya.assistant.data.model.ChatMessage
import com.maya.assistant.data.model.MessageSender

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val isVoice: Boolean = false,
    val attachmentUri: String? = null,
    val attachmentType: String? = null
) {
    fun toDomain(): ChatMessage {
        val senderEnum = try {
            MessageSender.valueOf(sender)
        } catch (e: Exception) {
            MessageSender.MAYA
        }
        return ChatMessage(
            id = id,
            sender = senderEnum,
            content = content,
            timestamp = timestamp,
            isVoice = isVoice,
            attachmentUri = attachmentUri,
            attachmentType = attachmentType
        )
    }

    companion object {
        fun fromDomain(msg: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = msg.id,
                sender = msg.sender.name,
                content = msg.content,
                timestamp = msg.timestamp,
                isVoice = msg.isVoice,
                attachmentUri = msg.attachmentUri,
                attachmentType = msg.attachmentType
            )
        }
    }
}
