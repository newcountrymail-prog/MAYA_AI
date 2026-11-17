package com.maya.assistant.voice

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.maya.assistant.ai.MayaPersonaEngine
import com.maya.assistant.data.local.datastore.PreferencesManager
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.data.model.UserSettings
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
    private val preferencesManager: PreferencesManager
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
        // If speaking, interrupt
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
        scope.launch {
            val settings = preferencesManager.userSettingsFlow.first()
            val response = personaEngine.generateResponse(query, settings)
            _lastResponse.value = response.text

            // Apply voice parameters
            ttsEngine?.setPitch(settings.voicePitch)
            ttsEngine?.setSpeechRate(settings.voiceSpeed)

            // Select language
            val locale = if (settings.voiceLanguage.startsWith("hi")) {
                Locale("hi", "IN")
            } else {
                Locale("en", "IN")
            }
            ttsEngine?.setLanguage(locale)

            // Speak response
            ttsEngine?.speak(response.text)
            onComplete?.invoke(response.text)
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
