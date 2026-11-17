package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.assistant.data.local.entity.AgentTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentTaskDao {

    @Query("SELECT * FROM agent_tasks ORDER BY timestamp DESC")
    fun getAllTasksFlow(): Flow<List<AgentTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AgentTaskEntity): Long

    @Update
    suspend fun updateTask(task: AgentTaskEntity)

    @Query("UPDATE agent_tasks SET status = :status, progressPercent = :progress WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, progress: Int)

    @Query("DELETE FROM agent_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
