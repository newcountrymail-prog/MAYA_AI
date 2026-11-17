package com.maya.assistant.ui.screens.agents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.ai.agent.AgentLiveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MultiAgentViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val agentSystem = mayaApp.multiAgentSystem

    val agentStates: StateFlow<Map<String, AgentLiveState>> = agentSystem.agentStates

    private val _pipelineProgress = MutableStateFlow(0)
    val pipelineProgress: StateFlow<Int> = _pipelineProgress.asStateFlow()

    private val _pipelineStatus = MutableStateFlow("Ready for assignment")
    val pipelineStatus: StateFlow<String> = _pipelineStatus.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    fun runCooperativeTask(taskTitle: String, prompt: String) {
        if (_isExecuting.value) return
        _isExecuting.value = true

        viewModelScope.launch {
            agentSystem.executeAgentPipeline(
                taskTitle = taskTitle,
                prompt = prompt,
                isFreeModeOnly = true
            ) { progress, status ->
                _pipelineProgress.value = progress
                _pipelineStatus.value = status
            }
            _isExecuting.value = false
        }
    }
}
