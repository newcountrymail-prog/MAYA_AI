package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maya.assistant.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM learned_tasks ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: MemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<MemoryEntity>)

    @Query("SELECT COUNT(*) FROM learned_tasks")
    suspend fun getCount(): Int

    @Query("DELETE FROM learned_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
