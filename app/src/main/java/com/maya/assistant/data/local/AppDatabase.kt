package com.maya.assistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maya.assistant.data.local.dao.AgentTaskDao
import com.maya.assistant.data.local.dao.AiProviderDao
import com.maya.assistant.data.local.dao.ChatMessageDao
import com.maya.assistant.data.local.dao.GeneratedImageDao
import com.maya.assistant.data.local.dao.MemoryDao
import com.maya.assistant.data.local.dao.ResearchReportDao
import com.maya.assistant.data.local.entity.AgentTaskEntity
import com.maya.assistant.data.local.entity.AiProviderEntity
import com.maya.assistant.data.local.entity.ChatMessageEntity
import com.maya.assistant.data.local.entity.GeneratedImageEntity
import com.maya.assistant.data.local.entity.MemoryEntity
import com.maya.assistant.data.local.entity.ResearchReportEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryEntity::class,
        AiProviderEntity::class,
        AgentTaskEntity::class,
        GeneratedImageEntity::class,
        ResearchReportEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun aiProviderDao(): AiProviderDao
    abstract fun agentTaskDao(): AgentTaskDao
    abstract fun generatedImageDao(): GeneratedImageDao
    abstract fun researchReportDao(): ResearchReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maya_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialTasks(getInstance(context).memoryDao())
                                populateInitialProviders(getInstance(context).aiProviderDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialTasks(dao: MemoryDao) {
            val initialTasks = listOf(
                MemoryEntity(
                    title = "gemini se poochho",
                    stepsCount = 8,
                    category = "sawaal",
                    isBuiltin = true,
                    description = "Gemini multimodal AI se quick query aur deep answer poochhein",
                    executionCommand = "ask_gemini"
                ),
                MemoryEntity(
                    title = "chatgpt se poochho",
                    stepsCount = 8,
                    category = "sawaal",
                    isBuiltin = true,
                    description = "ChatGPT se reasoning aur coding guidance poochhein",
                    executionCommand = "ask_chatgpt"
                ),
                MemoryEntity(
                    title = "gemini video",
                    stepsCount = 16,
                    category = "video prompt",
                    isBuiltin = true,
                    description = "Video analyze karke structured visual report generate karein",
                    executionCommand = "gemini_video_analysis"
                ),
                MemoryEntity(
                    title = "gemini image",
                    stepsCount = 17,
                    category = "image prompt",
                    isBuiltin = true,
                    description = "Photo inspect karke Hindi ya English me analysis karein",
                    executionCommand = "gemini_image_analysis"
                ),
                MemoryEntity(
                    title = "chatgpt image",
                    stepsCount = 14,
                    category = "image prompt",
                    isBuiltin = true,
                    description = "Image generation prompt craft karein aur refine karein",
                    executionCommand = "chatgpt_image_prompt"
                )
            )
            dao.insertAll(initialTasks)
        }

        private suspend fun populateInitialProviders(dao: AiProviderDao) {
            val defaultProviders = listOf(
                AiProviderEntity(
                    name = "Google Gemini Free Tier",
                    apiKey = "",
                    baseUrl = "https://generativelanguage.googleapis.com/v1beta",
                    authType = "ApiKeyHeader",
                    apiFormat = "Gemini",
                    selectedModel = "gemini-1.5-flash",
                    capabilities = "conversation,reasoning,coding,vision,research,documents",
                    priority = 1,
                    isEnabled = true,
                    pricingTier = "FreeTier",
                    connectionStatus = "Connected",
                    isDefault = true
                ),
                AiProviderEntity(
                    name = "Groq High-Speed",
                    apiKey = "",
                    baseUrl = "https://api.groq.com/openai/v1",
                    authType = "Bearer",
                    apiFormat = "OpenAI",
                    selectedModel = "llama-3.1-70b-versatile",
                    capabilities = "conversation,reasoning,coding,voice",
                    priority = 2,
                    isEnabled = true,
                    pricingTier = "FreeTier",
                    connectionStatus = "Connected",
                    isDefault = false
                ),
                AiProviderEntity(
                    name = "OpenRouter Free Models",
                    apiKey = "",
                    baseUrl = "https://openrouter.ai/api/v1",
                    authType = "Bearer",
                    apiFormat = "OpenAI",
                    selectedModel = "meta-llama/llama-3.1-8b-instruct:free",
                    capabilities = "conversation,reasoning,coding",
                    priority = 3,
                    isEnabled = true,
                    pricingTier = "VerifiedFree",
                    connectionStatus = "Connected",
                    isDefault = false
                ),
                AiProviderEntity(
                    name = "Pollinations Free Image Studio",
                    apiKey = "",
                    baseUrl = "https://image.pollinations.ai/prompt",
                    authType = "None",
                    apiFormat = "Custom",
                    selectedModel = "flux",
                    capabilities = "image_generation,image_editing",
                    priority = 1,
                    isEnabled = true,
                    pricingTier = "VerifiedFree",
                    connectionStatus = "Connected",
                    isDefault = false
                ),
                AiProviderEntity(
                    name = "OpenAI",
                    apiKey = "",
                    baseUrl = "https://api.openai.com/v1",
                    authType = "Bearer",
                    apiFormat = "OpenAI",
                    selectedModel = "gpt-4o",
                    capabilities = "conversation,reasoning,coding,vision,image_generation",
                    priority = 4,
                    isEnabled = false,
                    pricingTier = "Paid",
                    connectionStatus = "Untested",
                    isDefault = false
                ),
                AiProviderEntity(
                    name = "Anthropic Claude",
                    apiKey = "",
                    baseUrl = "https://api.anthropic.com/v1",
                    authType = "ApiKeyHeader",
                    apiFormat = "Anthropic",
                    selectedModel = "claude-3-5-sonnet-20241022",
                    capabilities = "conversation,reasoning,coding,documents",
                    priority = 5,
                    isEnabled = false,
                    pricingTier = "Paid",
                    connectionStatus = "Untested",
                    isDefault = false
                ),
                AiProviderEntity(
                    name = "DeepSeek",
                    apiKey = "",
                    baseUrl = "https://api.deepseek.com/v1",
                    authType = "Bearer",
                    apiFormat = "OpenAI",
                    selectedModel = "deepseek-chat",
                    capabilities = "conversation,coding,reasoning",
                    priority = 6,
                    isEnabled = false,
                    pricingTier = "FreeTier",
                    connectionStatus = "Untested",
                    isDefault = false
                )
            )
            dao.insertAll(defaultProviders)
        }
    }
}
