package com.maya.assistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.assistant.data.local.entity.AiProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiProviderDao {

    @Query("SELECT * FROM ai_providers ORDER BY priority ASC, id ASC")
    fun getAllProvidersFlow(): Flow<List<AiProviderEntity>>

    @Query("SELECT * FROM ai_providers WHERE isEnabled = 1 ORDER BY priority ASC")
    suspend fun getEnabledProviders(): List<AiProviderEntity>

    @Query("SELECT * FROM ai_providers WHERE id = :id LIMIT 1")
    suspend fun getProviderById(id: Long): AiProviderEntity?

    @Query("SELECT * FROM ai_providers WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProvider(): AiProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: AiProviderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(providers: List<AiProviderEntity>)

    @Update
    suspend fun updateProvider(provider: AiProviderEntity)

    @Query("UPDATE ai_providers SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE ai_providers SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)

    @Query("DELETE FROM ai_providers WHERE id = :id")
    suspend fun deleteProvider(id: Long)
}
