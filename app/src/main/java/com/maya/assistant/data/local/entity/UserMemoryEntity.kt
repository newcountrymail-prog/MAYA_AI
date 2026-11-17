package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_memories")
data class UserMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val content: String,
    val category: String, // "User preferences", "Projects", "Contact aliases", "Frequently used commands", "Task workflows", "Conversation summaries", "Device preferences", "Important notes"
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val tags: String = ""
) {
    companion object {
        const val CAT_USER_PREFERENCES = "User preferences"
        const val CAT_PROJECTS = "Projects"
        const val CAT_CONTACT_ALIASES = "Contact aliases"
        const val CAT_COMMANDS = "Frequently used commands"
        const val CAT_WORKFLOWS = "Task workflows"
        const val CAT_CONVERSATION_SUMMARIES = "Conversation summaries"
        const val CAT_DEVICE_PREFERENCES = "Device preferences"
        const val CAT_IMPORTANT_NOTES = "Important notes"

        val ALL_CATEGORIES = listOf(
            CAT_USER_PREFERENCES,
            CAT_PROJECTS,
            CAT_CONTACT_ALIASES,
            CAT_COMMANDS,
            CAT_WORKFLOWS,
            CAT_CONVERSATION_SUMMARIES,
            CAT_DEVICE_PREFERENCES,
            CAT_IMPORTANT_NOTES
        )
    }
}
