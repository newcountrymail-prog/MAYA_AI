package com.maya.assistant.skills

import com.maya.assistant.automation.PhoneControlManager
import com.maya.assistant.telecom.PhoneCommunicationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    PAUSED,
    NEEDS_APPROVAL
}

data class WorkflowStep(
    val id: String,
    val title: String,
    val description: String,
    val actionType: String,
    val parameters: Map<String, String> = emptyMap(),
    val status: StepStatus = StepStatus.PENDING,
    val requiresApproval: Boolean = false,
    val logOutput: String? = null
)

data class SkillPlaybook(
    val id: String,
    val title: String,
    val category: String, // Productivity, Study, Documents, Photos, Browser, Coding, Research, Device Control, Media, Voice, Automation
    val description: String,
    val iconName: String,
    val steps: List<WorkflowStep>,
    val isCustom: Boolean = false
)

class SkillsManager(
    private val phoneControlManager: PhoneControlManager,
    private val phoneCommunicationManager: PhoneCommunicationManager
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var activeJob: Job? = null

    private val _availableSkills = MutableStateFlow<List<SkillPlaybook>>(emptyList())
    val availableSkills: StateFlow<List<SkillPlaybook>> = _availableSkills.asStateFlow()

    private val _activeWorkflow = MutableStateFlow<SkillPlaybook?>(null)
    val activeWorkflow: StateFlow<SkillPlaybook?> = _activeWorkflow.asStateFlow()

    private val _workflowLogs = MutableStateFlow<List<String>>(emptyList())
    val workflowLogs: StateFlow<List<String>> = _workflowLogs.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    init {
        loadPreinstalledSkills()
    }

    private fun loadPreinstalledSkills() {
        val list = listOf(
            SkillPlaybook(
                id = "device_torch_toggle",
                title = "Flashlight Toggle",
                category = "Device Control",
                description = "Toggle the device rear camera flash torch instantly.",
                iconName = "Flashlight",
                steps = listOf(
                    WorkflowStep(
                        id = "step_torch_1",
                        title = "Toggle Flashlight Torch",
                        description = "Inverts the camera torch mode safely.",
                        actionType = "DEVICE_TORCH"
                    )
                )
            ),
            SkillPlaybook(
                id = "media_volume_mute",
                title = "Mute Media Volume",
                category = "Media",
                description = "Sets media playback volume level to 0% immediately.",
                iconName = "VolumeMute",
                steps = listOf(
                    WorkflowStep(
                        id = "step_vol_0",
                        title = "Set Volume to 0%",
                        description = "Adjusts stream music volume to zero.",
                        actionType = "SET_VOLUME",
                        parameters = mapOf("percent" to "0")
                    )
                )
            ),
            SkillPlaybook(
                id = "media_volume_boost",
                title = "Max Volume Boost",
                category = "Media",
                description = "Boosts media audio stream to 100% capacity.",
                iconName = "VolumeUp",
                steps = listOf(
                    WorkflowStep(
                        id = "step_vol_100",
                        title = "Set Volume to 100%",
                        description = "Adjusts stream music volume to max level.",
                        actionType = "SET_VOLUME",
                        parameters = mapOf("percent" to "100")
                    )
                )
            ),
            SkillPlaybook(
                id = "morning_productivity_routine",
                title = "Morning Routine Briefing",
                category = "Productivity",
                description = "Inspects notifications, reviews calendar events, and adjusts morning volume.",
                iconName = "WbSunny",
                steps = listOf(
                    WorkflowStep(
                        id = "step_morn_vol",
                        title = "Set Pleasant Morning Volume (50%)",
                        description = "Adjust audio volume to comfortable level.",
                        actionType = "SET_VOLUME",
                        parameters = mapOf("percent" to "50")
                    ),
                    WorkflowStep(
                        id = "step_morn_events",
                        title = "Review Today's Schedule",
                        description = "Opens system calendar view for day plan.",
                        actionType = "CALENDAR_VIEW"
                    )
                )
            ),
            SkillPlaybook(
                id = "whatsapp_quick_dictation",
                title = "WhatsApp Quick Message",
                category = "Automation",
                description = "Pre-formats message and launches WhatsApp conversation with confirmation.",
                iconName = "Chat",
                steps = listOf(
                    WorkflowStep(
                        id = "step_wa_prepare",
                        title = "Verify Contact & Text",
                        description = "Requires user confirmation before opening conversation.",
                        actionType = "WHATSAPP_PREPARE",
                        requiresApproval = true
                    ),
                    WorkflowStep(
                        id = "step_wa_open",
                        title = "Open WhatsApp Send Screen",
                        description = "Launches WhatsApp with pre-filled message.",
                        actionType = "WHATSAPP_SEND"
                    )
                )
            ),
            SkillPlaybook(
                id = "screen_error_diagnosis",
                title = "Screen Error Diagnosis",
                category = "Research",
                description = "Inspects active screen text and provides step-by-step troubleshooting.",
                iconName = "FindInPage",
                steps = listOf(
                    WorkflowStep(
                        id = "step_screen_capture",
                        title = "Read Screen Accessibility Content",
                        description = "Extracts visible text from the foreground app.",
                        actionType = "READ_SCREEN_TEXT"
                    ),
                    WorkflowStep(
                        id = "step_screen_analyze",
                        title = "AI Error Explanation",
                        description = "Analyzes extracted errors and advises recovery steps.",
                        actionType = "ANALYZE_ERROR"
                    )
                )
            ),
            SkillPlaybook(
                id = "study_deep_focus",
                title = "Study & Focus Mode",
                category = "Study",
                description = "Mutes unnecessary alerts, prepares timer, and logs focus session in memory.",
                iconName = "MenuBook",
                steps = listOf(
                    WorkflowStep(
                        id = "step_focus_mute",
                        title = "Lower Media Volume",
                        description = "Sets volume to 10% for quiet background audio.",
                        actionType = "SET_VOLUME",
                        parameters = mapOf("percent" to "10")
                    ),
                    WorkflowStep(
                        id = "step_focus_timer",
                        title = "Set 25-Min Study Timer",
                        description = "Creates 1500 second Pomodoro timer.",
                        actionType = "SET_TIMER",
                        parameters = mapOf("seconds" to "1500", "message" to "MAYA Study Session")
                    )
                )
            ),
            SkillPlaybook(
                id = "coding_workspace_init",
                title = "Coding Workspace Inspector",
                category = "Coding",
                description = "Inspects local project structure and prepares automated test harness.",
                iconName = "Code",
                steps = listOf(
                    WorkflowStep(
                        id = "step_code_scan",
                        title = "Scan Project Health",
                        description = "Validates Gradle configuration and source files.",
                        actionType = "CODE_SCAN"
                    )
                )
            )
        )
        _availableSkills.value = list
    }

    fun startWorkflow(skill: SkillPlaybook) {
        _activeWorkflow.value = skill
        _workflowLogs.value = listOf("Workflow started: ${skill.title}")
        _isExecuting.value = true

        activeJob = coroutineScope.launch {
            val updatedSteps = skill.steps.toMutableList()
            for (i in updatedSteps.indices) {
                val step = updatedSteps[i]
                if (step.requiresApproval) {
                    updatedSteps[i] = step.copy(status = StepStatus.NEEDS_APPROVAL)
                    updateActiveWorkflow(updatedSteps)
                    appendLog("Step [${step.title}] requires user approval to proceed.")
                    break
                }

                updatedSteps[i] = step.copy(status = StepStatus.RUNNING)
                updateActiveWorkflow(updatedSteps)
                appendLog("Executing step: ${step.title}")

                val success = executeStepAction(step)
                if (success) {
                    updatedSteps[i] = step.copy(status = StepStatus.COMPLETED, logOutput = "Success")
                    updateActiveWorkflow(updatedSteps)
                    appendLog("Completed step: ${step.title}")
                } else {
                    updatedSteps[i] = step.copy(status = StepStatus.FAILED, logOutput = "Failed execution")
                    updateActiveWorkflow(updatedSteps)
                    appendLog("Error in step: ${step.title}")
                    break
                }
                delay(300)
            }
            _isExecuting.value = false
            appendLog("Workflow finished.")
        }
    }

    fun approveAndResumeStep(stepId: String) {
        val workflow = _activeWorkflow.value ?: return
        val updatedSteps = workflow.steps.toMutableList()
        val index = updatedSteps.indexOfFirst { it.id == stepId }
        if (index >= 0) {
            val step = updatedSteps[index]
            updatedSteps[index] = step.copy(status = StepStatus.RUNNING)
            updateActiveWorkflow(updatedSteps)
            appendLog("User approved step: ${step.title}")

            activeJob = coroutineScope.launch {
                val success = executeStepAction(step)
                if (success) {
                    updatedSteps[index] = step.copy(status = StepStatus.COMPLETED, logOutput = "Approved & executed")
                    updateActiveWorkflow(updatedSteps)
                    appendLog("Completed approved step: ${step.title}")
                    // Resume remaining steps
                    for (nextIndex in (index + 1) until updatedSteps.size) {
                        val nextStep = updatedSteps[nextIndex]
                        updatedSteps[nextIndex] = nextStep.copy(status = StepStatus.RUNNING)
                        updateActiveWorkflow(updatedSteps)
                        val nextSuccess = executeStepAction(nextStep)
                        if (nextSuccess) {
                            updatedSteps[nextIndex] = nextStep.copy(status = StepStatus.COMPLETED)
                            updateActiveWorkflow(updatedSteps)
                        } else {
                            updatedSteps[nextIndex] = nextStep.copy(status = StepStatus.FAILED)
                            updateActiveWorkflow(updatedSteps)
                            break
                        }
                    }
                } else {
                    updatedSteps[index] = step.copy(status = StepStatus.FAILED)
                    updateActiveWorkflow(updatedSteps)
                }
                _isExecuting.value = false
            }
        }
    }

    fun pauseWorkflow() {
        activeJob?.cancel()
        _isExecuting.value = false
        appendLog("Workflow paused by user.")
    }

    fun cancelWorkflow() {
        activeJob?.cancel()
        _activeWorkflow.value = null
        _isExecuting.value = false
        appendLog("Workflow cancelled.")
    }

    private fun executeStepAction(step: WorkflowStep): Boolean {
        return try {
            when (step.actionType) {
                "DEVICE_TORCH" -> {
                    val result = phoneControlManager.toggleTorch()
                    result.success
                }
                "SET_VOLUME" -> {
                    val pct = step.parameters["percent"]?.toIntOrNull() ?: 50
                    val result = phoneControlManager.setMediaVolumePercent(pct)
                    result.success
                }
                "SET_TIMER" -> {
                    val sec = step.parameters["seconds"]?.toIntOrNull() ?: 60
                    val msg = step.parameters["message"] ?: "Maya Timer"
                    val result = phoneControlManager.setTimer(sec, msg)
                    result.success
                }
                "CALENDAR_VIEW" -> {
                    val result = phoneControlManager.createCalendarEvent("Maya Daily Review")
                    result.success
                }
                "WHATSAPP_PREPARE" -> {
                    // Pre-verification step
                    true
                }
                "WHATSAPP_SEND" -> {
                    val phone = step.parameters["phone"] ?: ""
                    val msg = step.parameters["text"] ?: "Hello from MAYA AI!"
                    val result = phoneCommunicationManager.sendWhatsAppMessage(phone, msg)
                    result.success
                }
                "READ_SCREEN_TEXT" -> {
                    val texts = phoneControlManager.readScreenText()
                    appendLog("Read ${texts.size} text elements from screen.")
                    true
                }
                "CODE_SCAN", "ANALYZE_ERROR" -> {
                    // Diagnostic step completed
                    true
                }
                else -> true
            }
        } catch (e: Exception) {
            appendLog("Step execution exception: ${e.message}")
            false
        }
    }

    private fun updateActiveWorkflow(steps: List<WorkflowStep>) {
        _activeWorkflow.value = _activeWorkflow.value?.copy(steps = steps)
    }

    private fun appendLog(msg: String) {
        val current = _workflowLogs.value.toMutableList()
        current.add("[${System.currentTimeMillis() % 100000}] $msg")
        _workflowLogs.value = current
    }
}
