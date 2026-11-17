package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maya.assistant.data.local.entity.ResearchReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchReportDao {

    @Query("SELECT * FROM research_reports ORDER BY timestamp DESC")
    fun getAllReportsFlow(): Flow<List<ResearchReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ResearchReportEntity): Long

    @Query("DELETE FROM research_reports WHERE id = :id")
    suspend fun deleteReport(id: Long)
}
