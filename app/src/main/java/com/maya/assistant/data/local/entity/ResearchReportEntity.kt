package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "research_reports")
data class ResearchReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val summary: String,
    val fullReport: String,
    val sourcesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
