package com.maya.assistant.data.repository

import com.maya.assistant.data.local.dao.UserMemoryDao
import com.maya.assistant.data.local.entity.UserMemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserMemoryRepository(private val dao: UserMemoryDao) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    fun getAllMemoriesFlow(): Flow<List<UserMemoryEntity>> = dao.getAllMemoriesFlow()

    fun getMemoriesByCategoryFlow(category: String): Flow<List<UserMemoryEntity>> =
        dao.getMemoriesByCategoryFlow(category)

    fun searchMemoriesFlow(query: String): Flow<List<UserMemoryEntity>> =
        dao.searchMemoriesFlow(query)

    suspend fun addMemory(
        key: String,
        content: String,
        category: String,
        isPinned: Boolean = false,
        tags: String = ""
    ): Long {
        val memory = UserMemoryEntity(
            key = key.trim(),
            content = content.trim(),
            category = category,
            isPinned = isPinned,
            tags = tags.trim()
        )
        return dao.insertMemory(memory)
    }

    suspend fun updateMemory(memory: UserMemoryEntity) {
        dao.updateMemory(memory)
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteMemory(id)
    }

    suspend fun exportToJson(): String {
        val all = dao.getAllMemories()
        return json.encodeToString(all)
    }

    suspend fun importFromJson(jsonString: String): Result<Int> {
        return try {
            val list = json.decodeFromString<List<UserMemoryEntity>>(jsonString)
            dao.insertAll(list)
            Result.success(list.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }

    suspend fun getCount(): Int = dao.getCount()
}
