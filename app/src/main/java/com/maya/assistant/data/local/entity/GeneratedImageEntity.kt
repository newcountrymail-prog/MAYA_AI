package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val imageUrl: String,
    val localPath: String? = null,
    val aspectRatio: String = "1:1",
    val providerUsed: String = "Free Image Studio",
    val timestamp: Long = System.currentTimeMillis()
)
