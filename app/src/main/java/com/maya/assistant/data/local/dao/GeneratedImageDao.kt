package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maya.assistant.data.local.entity.GeneratedImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedImageDao {

    @Query("SELECT * FROM generated_images ORDER BY timestamp DESC")
    fun getAllImagesFlow(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: GeneratedImageEntity): Long

    @Query("DELETE FROM generated_images WHERE id = :id")
    suspend fun deleteImage(id: Long)
}
