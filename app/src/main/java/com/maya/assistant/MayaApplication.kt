package com.maya.assistant

import android.app.Application
import com.maya.assistant.data.local.AppDatabase
import com.maya.assistant.data.local.datastore.PreferencesManager
import com.maya.assistant.data.repository.ChatRepository
import com.maya.assistant.data.repository.MemoryRepository
import com.maya.assistant.voice.VoiceEngine

class MayaApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var chatRepository: ChatRepository
        private set

    lateinit var memoryRepository: MemoryRepository
        private set

    lateinit var voiceEngine: VoiceEngine
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        chatRepository = ChatRepository(database.chatMessageDao())
        memoryRepository = MemoryRepository(database.memoryDao())
        voiceEngine = VoiceEngine(this, preferencesManager)
    }

    companion object {
        lateinit var instance: MayaApplication
            private set
    }
}
