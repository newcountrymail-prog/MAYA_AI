package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.assistant.data.local.entity.UserMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMemoryDao {

    @Query("SELECT * FROM user_memories ORDER BY isPinned DESC, timestamp DESC")
    fun getAllMemoriesFlow(): Flow<List<UserMemoryEntity>>

    @Query("SELECT * FROM user_memories ORDER BY timestamp DESC")
    suspend fun getAllMemories(): List<UserMemoryEntity>

    @Query("SELECT * FROM user_memories WHERE category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getMemoriesByCategoryFlow(category: String): Flow<List<UserMemoryEntity>>

    @Query("SELECT * FROM user_memories WHERE `key` LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMemoriesFlow(query: String): Flow<List<UserMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: UserMemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(memories: List<UserMemoryEntity>)

    @Update
    suspend fun updateMemory(memory: UserMemoryEntity)

    @Query("DELETE FROM user_memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM user_memories")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM user_memories")
    suspend fun getCount(): Int
}
