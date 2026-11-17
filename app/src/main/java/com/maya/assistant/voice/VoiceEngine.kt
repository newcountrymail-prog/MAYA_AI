package com.maya.assistant.voice

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.maya.assistant.ai.ExecutionResult
import com.maya.assistant.ai.MayaPersonaEngine
import com.maya.assistant.ai.MultiModelOrchestrator
import com.maya.assistant.data.local.datastore.PreferencesManager
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.tools.MayaToolsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceEngine(
    private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val orchestratorProvider: () -> MultiModelOrchestrator,
    private val toolsManagerProvider: () -> MayaToolsManager
) {

    private val personaEngine = MayaPersonaEngine()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _orbState = MutableStateFlow(OrbState.Idle)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    private val _rmsAmplitude = MutableStateFlow(0f)
    val rmsAmplitude: StateFlow<Float> = _rmsAmplitude.asStateFlow()

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()

    private val _lastResponse = MutableStateFlow("")
    val lastResponse: StateFlow<String> = _lastResponse.asStateFlow()

    private var speechRecognizer: MayaSpeechRecognizer? = null
    private var ttsEngine: MayaTtsEngine? = null

    init {
        ttsEngine = MayaTtsEngine(context) { isSpeaking ->
            if (isSpeaking) {
                _orbState.value = OrbState.Speaking
            } else {
                if (_orbState.value == OrbState.Speaking) {
                    _orbState.value = OrbState.Idle
                }
            }
        }

        speechRecognizer = MayaSpeechRecognizer(
            context = context,
            onRmsChangedListener = { rms ->
                _rmsAmplitude.value = rms
            },
            onPartialResult = { partial ->
                _transcription.value = partial
            },
            onFinalResult = { finalResult ->
                _transcription.value = finalResult
                if (finalResult.isNotBlank()) {
                    processQuery(finalResult)
                } else {
                    _orbState.value = OrbState.Idle
                }
            },
            onErrorOccurred = { _ ->
                _orbState.value = OrbState.Idle
            }
        )
    }

    fun startListening() {
        ttsEngine?.stop()
        _orbState.value = OrbState.Listening
        _transcription.value = ""

        scope.launch {
            val settings = preferencesManager.userSettingsFlow.first()
            val language = settings.voiceLanguage
            speechRecognizer?.startListening(language)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        if (_orbState.value == OrbState.Listening) {
            _orbState.value = OrbState.Idle
        }
    }

    fun interrupt() {
        ttsEngine?.stop()
        speechRecognizer?.cancel()
        _orbState.value = OrbState.Idle
    }

    fun processQuery(query: String, onComplete: ((String) -> Unit)? = null) {
        _orbState.value = OrbState.Processing
        val cleanQuery = query.trim().lowercase(Locale.ROOT)

        scope.launch(Dispatchers.IO) {
            val tools = toolsManagerProvider()
            var directSpeechResponse: String? = null

            // 1. Check Tool Intent: Flashlight / Torch
            if (cleanQuery.contains("torch") || cleanQuery.contains("flashlight")) {
                val res = tools.toggleTorch()
                directSpeechResponse = res.speechResponse
            }
            // 2. Check Tool Intent: Weather / Mausam
            else if (cleanQuery.contains("mausam") || cleanQuery.contains("weather")) {
                val location = if (cleanQuery.contains("in ")) {
                    cleanQuery.substringAfter("in ").trim()
                } else {
                    ""
                }
                val res = tools.getLiveWeather(location)
                directSpeechResponse = res.speechResponse
            }
            // 3. Check Tool Intent: Calculator
            else if (cleanQuery.contains("calculate") || cleanQuery.contains("kitna hota") || cleanQuery.contains("+") || cleanQuery.contains("*")) {
                val mathExpr = cleanQuery.replace("calculate", "").replace("kitna hota hai", "").trim()
                if (mathExpr.any { it.isDigit() }) {
                    val res = tools.calculateExpression(mathExpr)
                    directSpeechResponse = res.speechResponse
                }
            }
            // 4. Check Tool Intent: Memory / Yaad rakhna
            else if (cleanQuery.contains("yaad rakhna") || cleanQuery.contains("remember that")) {
                val content = query.replace("yaad rakhna", "", ignoreCase = true)
                    .replace("remember that", "", ignoreCase = true)
                    .trim()
                val res = tools.saveMemoryFromSpeech("User Note", content)
                directSpeechResponse = res.speechResponse
            }
            // 5. Check Tool Intent: App Launch
            else if (cleanQuery.startsWith("open ") || cleanQuery.endsWith("kholo")) {
                val appName = query.replace("open ", "", ignoreCase = true)
                    .replace("kholo", "", ignoreCase = true)
                    .trim()
                val res = tools.launchApp(appName)
                directSpeechResponse = res.speechResponse
            }

            // 6. If no tool matched, route to Real MultiModel AI Engine (Pollinations Free Zero-Key or User Key)
            val finalResponseText = if (directSpeechResponse != null) {
                directSpeechResponse
            } else {
                val orchestrator = orchestratorProvider()
                val aiResult = orchestrator.routeAndExecute(
                    capability = "conversation",
                    prompt = query,
                    isFreeModeOnly = true
                )
                when (aiResult) {
                    is ExecutionResult.Success -> aiResult.content
                    is ExecutionResult.Failure -> {
                        // Fallback to Hinglish persona template
                        val settings = preferencesManager.userSettingsFlow.first()
                        personaEngine.generateResponse(query, settings).text
                    }
                }
            }

            // Update state on Main thread
            launch(Dispatchers.Main) {
                _lastResponse.value = finalResponseText
                val settings = preferencesManager.userSettingsFlow.first()

                ttsEngine?.setPitch(settings.voicePitch)
                ttsEngine?.setSpeechRate(settings.voiceSpeed)

                val locale = if (settings.voiceLanguage.startsWith("hi")) {
                    Locale("hi", "IN")
                } else {
                    Locale("en", "IN")
                }
                ttsEngine?.setLanguage(locale)
                ttsEngine?.speak(finalResponseText)
                onComplete?.invoke(finalResponseText)
            }
        }
    }

    fun speakDirect(text: String) {
        scope.launch {
            val settings = preferencesManager.userSettingsFlow.first()
            ttsEngine?.setPitch(settings.voicePitch)
            ttsEngine?.setSpeechRate(settings.voiceSpeed)
            ttsEngine?.speak(text)
        }
    }

    fun setOrbState(state: OrbState) {
        _orbState.value = state
    }
}
