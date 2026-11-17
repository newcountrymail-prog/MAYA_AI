package com.maya.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maya.assistant.data.model.LearnedTask

@Entity(tableName = "learned_tasks")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val stepsCount: Int,
    val category: String,
    val isBuiltin: Boolean = true,
    val description: String = "",
    val executionCommand: String = ""
) {
    fun toDomain(): LearnedTask = LearnedTask(
        id = id,
        title = title,
        stepsCount = stepsCount,
        category = category,
        isBuiltin = isBuiltin,
        description = description,
        executionCommand = executionCommand
    )

    companion object {
        fun fromDomain(task: LearnedTask): MemoryEntity = MemoryEntity(
            id = task.id,
            title = task.title,
            stepsCount = task.stepsCount,
            category = task.category,
            isBuiltin = task.isBuiltin,
            description = task.description,
            executionCommand = task.executionCommand
        )
    }
}
