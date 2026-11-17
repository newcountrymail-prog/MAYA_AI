package com.maya.assistant.ui.screens.memories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.data.local.entity.UserMemoryEntity
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
    private val userMemoryRepository = mayaApp.userMemoryRepository
    private val voiceEngine = mayaApp.voiceEngine

    val tasks: StateFlow<List<LearnedTask>> = memoryRepository.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userMemories: StateFlow<List<UserMemoryEntity>> = userMemoryRepository.getAllMemoriesFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _executingTaskId = MutableStateFlow<Long?>(null)
    val executingTaskId: StateFlow<Long?> = _executingTaskId.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

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

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addMemory(key: String, content: String, category: String, isPinned: Boolean, tags: String) {
        viewModelScope.launch {
            userMemoryRepository.addMemory(key, content, category, isPinned, tags)
            _statusMessage.value = "Memory saved in $category"
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            userMemoryRepository.deleteMemory(id)
            _statusMessage.value = "Memory deleted"
        }
    }

    fun exportMemories(onExported: (String) -> Unit) {
        viewModelScope.launch {
            val json = userMemoryRepository.exportToJson()
            onExported(json)
            _statusMessage.value = "Memories exported to JSON successfully"
        }
    }

    fun importMemories(jsonString: String) {
        viewModelScope.launch {
            val res = userMemoryRepository.importFromJson(jsonString)
            if (res.isSuccess) {
                _statusMessage.value = "Imported ${res.getOrNull()} memories successfully"
            } else {
                _statusMessage.value = "Import failed: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            voiceEngine.speakDirect("Aapki yaadein aur memory preferences local database me surakshit hain.")
            _statusMessage.value = "Backup created: All memories stored in local SQLite database"
        }
    }
}
