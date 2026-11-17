package com.maya.assistant.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class MayaSpeechRecognizer(
    private val context: Context,
    private val onRmsChangedListener: (Float) -> Unit = {},
    private val onPartialResult: (String) -> Unit = {},
    private val onFinalResult: (String) -> Unit = {},
    private val onErrorOccurred: (Int) -> Unit = {}
) {

    private val tag = "MayaSpeechRecognizer"
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d(tag, "Microphone ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(tag, "Speech detected")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        onRmsChangedListener(rmsdB)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                        Log.d(tag, "End of speech")
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        Log.w(tag, "Speech recognizer error: $error")
                        onErrorOccurred(error)
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        Log.d(tag, "Recognized text: $text")
                        onFinalResult(text)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        onPartialResult(text)
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } else {
            Log.w(tag, "Speech recognition not available on device")
        }
    }

    fun startListening(languageCode: String = "hi-IN") {
        if (speechRecognizer == null) {
            initRecognizer()
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            isListening = true
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            Log.e(tag, "Error starting speech recognition: ${e.message}")
            onErrorOccurred(-1)
        }
    }

    fun stopListening() {
        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(tag, "Error stopping recognition: ${e.message}")
            } finally {
                isListening = false
            }
        }
    }

    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.e(tag, "Error cancelling recognition: ${e.message}")
        } finally {
            isListening = false
        }
    }

    fun destroy() {
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
