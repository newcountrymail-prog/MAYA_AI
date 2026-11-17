package com.maya.assistant.data.repository

import com.maya.assistant.data.local.dao.MemoryDao
import com.maya.assistant.data.local.entity.MemoryEntity
import com.maya.assistant.data.model.LearnedTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryRepository(private val memoryDao: MemoryDao) {

    fun getAllTasks(): Flow<List<LearnedTask>> {
        return memoryDao.getAllTasksFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addTask(task: LearnedTask): Long {
        return memoryDao.insertTask(MemoryEntity.fromDomain(task))
    }

    suspend fun deleteTask(id: Long) {
        memoryDao.deleteTask(id)
    }
}
