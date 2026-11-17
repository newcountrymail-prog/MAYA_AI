package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_tasks")
data class AgentTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val assignedAgent: String,
    val status: String = "Queued", // Queued, Planning, Running, WaitingForApproval, Paused, Completed, Failed, Cancelled
    val progressPercent: Int = 0,
    val inputPrompt: String,
    val outputResult: String? = null,
    val checkpointData: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
