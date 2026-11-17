package com.maya.assistant

import android.app.Application
import com.maya.assistant.ai.MultiModelOrchestrator
import com.maya.assistant.ai.agent.MultiAgentSystem
import com.maya.assistant.ai.document.DocumentsEngine
import com.maya.assistant.ai.image.ImageStudioEngine
import com.maya.assistant.ai.opencode.OpenCodeEngine
import com.maya.assistant.ai.research.BrowserAndResearchEngine
import com.maya.assistant.data.local.AppDatabase
import com.maya.assistant.data.local.datastore.PreferencesManager
import com.maya.assistant.data.repository.AiProviderRepository
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

    lateinit var aiProviderRepository: AiProviderRepository
        private set

    lateinit var multiModelOrchestrator: MultiModelOrchestrator
        private set

    lateinit var multiAgentSystem: MultiAgentSystem
        private set

    lateinit var openCodeEngine: OpenCodeEngine
        private set

    lateinit var imageStudioEngine: ImageStudioEngine
        private set

    lateinit var browserAndResearchEngine: BrowserAndResearchEngine
        private set

    lateinit var documentsEngine: DocumentsEngine
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
        aiProviderRepository = AiProviderRepository(database.aiProviderDao())

        multiModelOrchestrator = MultiModelOrchestrator(aiProviderRepository)
        multiAgentSystem = MultiAgentSystem(multiModelOrchestrator)
        openCodeEngine = OpenCodeEngine()
        imageStudioEngine = ImageStudioEngine()
        browserAndResearchEngine = BrowserAndResearchEngine()
        documentsEngine = DocumentsEngine()

        voiceEngine = VoiceEngine(this, preferencesManager)
    }

    companion object {
        lateinit var instance: MayaApplication
            private set
    }
}
