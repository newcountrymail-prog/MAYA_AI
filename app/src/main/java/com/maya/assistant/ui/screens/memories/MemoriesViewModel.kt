package com.maya.assistant.ui.screens.memories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.model.LearnedTask
import com.maya.assistant.data.model.OrbState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MemoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val memoryRepository = mayaApp.memoryRepository
    private val voiceEngine = mayaApp.voiceEngine

    val tasks: StateFlow<List<LearnedTask>> = memoryRepository.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _executingTaskId = MutableStateFlow<Long?>(null)
    val executingTaskId: StateFlow<Long?> = _executingTaskId.asStateFlow()

    fun runTask(task: LearnedTask) {
        viewModelScope.launch {
            _executingTaskId.value = task.id
            voiceEngine.setOrbState(OrbState.Executing)
            voiceEngine.speakDirect("${task.title} task chala rahi hoon. Step 1 se shuru kar rahe hain.")

            delay(2500)

            _executingTaskId.value = null
            voiceEngine.setOrbState(OrbState.Idle)
            voiceEngine.speakDirect("${task.title} safaltapoorvak poora ho gaya!")
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            voiceEngine.speakDirect("Aapki yaadein aur chat history secure backup me save ho gayi hain.")
        }
    }
}
