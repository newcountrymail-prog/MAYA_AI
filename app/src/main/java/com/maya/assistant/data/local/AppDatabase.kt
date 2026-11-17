package com.maya.assistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maya.assistant.data.local.dao.ChatMessageDao
import com.maya.assistant.data.local.dao.MemoryDao
import com.maya.assistant.data.local.entity.ChatMessageEntity
import com.maya.assistant.data.local.entity.MemoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ChatMessageEntity::class, MemoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun memoryDao(): MemoryDao

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
    }
}
