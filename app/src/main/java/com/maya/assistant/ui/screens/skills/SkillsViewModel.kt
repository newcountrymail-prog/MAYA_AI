package com.maya.assistant.ui.screens.skills

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.maya.assistant.MayaApplication
import com.maya.assistant.skills.SkillPlaybook
import com.maya.assistant.skills.SkillsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SkillsViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    val skillsManager: SkillsManager = mayaApp.skillsManager

    val availableSkills: StateFlow<List<SkillPlaybook>> = skillsManager.availableSkills
    val activeWorkflow: StateFlow<SkillPlaybook?> = skillsManager.activeWorkflow
    val workflowLogs: StateFlow<List<String>> = skillsManager.workflowLogs
    val isExecuting: StateFlow<Boolean> = skillsManager.isExecuting

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun startWorkflow(skill: SkillPlaybook) {
        skillsManager.startWorkflow(skill)
    }

    fun approveStep(stepId: String) {
        skillsManager.approveAndResumeStep(stepId)
    }

    fun pauseWorkflow() {
        skillsManager.pauseWorkflow()
    }

    fun cancelWorkflow() {
        skillsManager.cancelWorkflow()
    }
}
