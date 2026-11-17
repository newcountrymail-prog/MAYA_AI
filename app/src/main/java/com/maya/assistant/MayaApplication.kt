package com.maya.assistant

import android.app.Application
import com.maya.assistant.ai.MultiModelOrchestrator
import com.maya.assistant.ai.agent.MultiAgentSystem
import com.maya.assistant.ai.document.DocumentsEngine
import com.maya.assistant.ai.image.ImageStudioEngine
import com.maya.assistant.ai.opencode.OpenCodeEngine
import com.maya.assistant.ai.research.BrowserAndResearchEngine
import com.maya.assistant.automation.PhoneControlManager
import com.maya.assistant.cloud.CloudJobManager
import com.maya.assistant.data.local.AppDatabase
import com.maya.assistant.data.local.datastore.PreferencesManager
import com.maya.assistant.data.repository.AiProviderRepository
import com.maya.assistant.data.repository.ChatRepository
import com.maya.assistant.data.repository.MemoryRepository
import com.maya.assistant.data.repository.UserMemoryRepository
import com.maya.assistant.skills.SkillsManager
import com.maya.assistant.telecom.PhoneCommunicationManager
import com.maya.assistant.vision.ScreenVisionManager
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

    lateinit var userMemoryRepository: UserMemoryRepository
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

    lateinit var phoneControlManager: PhoneControlManager
        private set

    lateinit var phoneCommunicationManager: PhoneCommunicationManager
        private set

    lateinit var screenVisionManager: ScreenVisionManager
        private set

    lateinit var skillsManager: SkillsManager
        private set

    lateinit var cloudJobManager: CloudJobManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        chatRepository = ChatRepository(database.chatMessageDao())
        memoryRepository = MemoryRepository(database.memoryDao())
        userMemoryRepository = UserMemoryRepository(database.userMemoryDao())
        aiProviderRepository = AiProviderRepository(database.aiProviderDao())

        multiModelOrchestrator = MultiModelOrchestrator(aiProviderRepository)
        multiAgentSystem = MultiAgentSystem(multiModelOrchestrator)
        openCodeEngine = OpenCodeEngine()
        imageStudioEngine = ImageStudioEngine()
        browserAndResearchEngine = BrowserAndResearchEngine()
        documentsEngine = DocumentsEngine()

        voiceEngine = VoiceEngine(this, preferencesManager)

        phoneControlManager = PhoneControlManager(this)
        phoneCommunicationManager = PhoneCommunicationManager(this)
        screenVisionManager = ScreenVisionManager(this, multiModelOrchestrator)
        skillsManager = SkillsManager(phoneControlManager, phoneCommunicationManager)
        cloudJobManager = CloudJobManager(multiModelOrchestrator)
    }

    companion object {
        lateinit var instance: MayaApplication
            private set
    }
}
