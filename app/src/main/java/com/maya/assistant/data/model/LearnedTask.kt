package com.maya.assistant.data.model

data class LearnedTask(
    val id: Long = 0,
    val title: String,
    val stepsCount: Int,
    val category: String,
    val isBuiltin: Boolean = true,
    val description: String = "",
    val executionCommand: String = ""
)
