package com.maya.assistant.data.repository

import com.maya.assistant.data.local.dao.ChatMessageDao
import com.maya.assistant.data.local.entity.ChatMessageEntity
import com.maya.assistant.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    fun getAllMessages(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessagesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun sendMessage(message: ChatMessage): Long {
        return chatMessageDao.insertMessage(ChatMessageEntity.fromDomain(message))
    }

    suspend fun clearChat() {
        chatMessageDao.clearAll()
    }
}
