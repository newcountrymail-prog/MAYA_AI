package com.maya.assistant.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

class MayaTtsEngine(
    private val context: Context,
    private val onPlaybackStateChanged: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private val tag = "MayaTtsEngine"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private var currentLanguage = Locale("hi", "IN")
    private var speechRate = 1.0f
    private var speechPitch = 1.05f // Natural pleasant female timbre

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            applyLanguageAndVoice()
            setupProgressListener()
            Log.d(tag, "MAYA TTS Engine successfully initialized with female voice target.")
        } else {
            Log.e(tag, "Failed to initialize Android TextToSpeech engine. Status: $status")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onPlaybackStateChanged(true)
            }

            override fun onDone(utteranceId: String?) {
                onPlaybackStateChanged(false)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onPlaybackStateChanged(false)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                onPlaybackStateChanged(false)
                Log.w(tag, "TTS utterance error: $errorCode for id $utteranceId")
            }
        })
    }

    private fun applyLanguageAndVoice() {
        val ttsEngine = tts ?: return
        if (!isInitialized) return

        // Set Language with fallback
        val result = ttsEngine.setLanguage(currentLanguage)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English (India) or English (US)
            ttsEngine.setLanguage(Locale("en", "IN"))
        }

        // Attempt to find and set a high quality natural female voice
        try {
            val availableVoices = ttsEngine.voices
            if (!availableVoices.isNullOrEmpty()) {
                val femaleVoice = availableVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    (name.contains("female") || name.contains("woman") || name.contains("-f-") || name.contains("f0") || name.contains("fem")) &&
                            (voice.locale.language == currentLanguage.language || voice.locale.language == "hi" || voice.locale.language == "en")
                } ?: availableVoices.firstOrNull { voice ->
                    voice.locale.language == currentLanguage.language && !voice.isNetworkConnectionRequired
                }

                if (femaleVoice != null) {
                    ttsEngine.voice = femaleVoice
                    Log.d(tag, "Selected female voice: ${femaleVoice.name}")
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Error setting specialized female voice: ${e.message}")
        }

        ttsEngine.setPitch(speechPitch)
        ttsEngine.setSpeechRate(speechRate)
    }

    fun speak(text: String, utteranceId: String = "maya_speech_${System.currentTimeMillis()}") {
        if (!isInitialized) {
            Log.w(tag, "TTS not yet initialized")
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        try {
            tts?.stop()
            onPlaybackStateChanged(false)
        } catch (e: Exception) {
            Log.e(tag, "Error stopping TTS: ${e.message}")
        }
    }

    fun setPitch(pitch: Float) {
        speechPitch = pitch
        tts?.setPitch(pitch)
    }

    fun setSpeechRate(rate: Float) {
        speechRate = rate
        tts?.setSpeechRate(rate)
    }

    fun setLanguage(locale: Locale) {
        currentLanguage = locale
        applyLanguageAndVoice()
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
