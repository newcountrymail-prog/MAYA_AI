package com.maya.assistant.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.maya.assistant.ai.MultiModelOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

data class VisionAnalysisResult(
    val success: Boolean,
    val summary: String,
    val rawText: String? = null,
    val identifiedObjects: List<String> = emptyList(),
    val extractedCodeOrError: String? = null,
    val privacyBannerText: String = "Processed with user consent"
)

enum class VisionTaskType {
    OCR_EXTRACT,
    SCREENSHOT_ERROR_EXPLAIN,
    DOCUMENT_SUMMARY,
    OBJECT_DESCRIPTION,
    DIAGRAM_ANALYSIS,
    TRANSLATION
}

class ScreenVisionManager(
    private val context: Context,
    private val orchestrator: MultiModelOrchestrator
) {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _privacyStatus = MutableStateFlow<String?>(null)
    val privacyStatus: StateFlow<String?> = _privacyStatus.asStateFlow()

    private val _lastAnalysisResult = MutableStateFlow<VisionAnalysisResult?>(null)
    val lastAnalysisResult: StateFlow<VisionAnalysisResult?> = _lastAnalysisResult.asStateFlow()

    suspend fun analyzeImageFile(
        file: File,
        taskType: VisionTaskType,
        userQuery: String? = null
    ): VisionAnalysisResult = withContext(Dispatchers.IO) {
        _isProcessing.value = true
        _privacyStatus.value = "Active: Analyzing image file locally & sending visual prompt to AI model"

        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            if (bitmap == null) {
                _isProcessing.value = false
                _privacyStatus.value = null
                return@withContext VisionAnalysisResult(
                    success = false,
                    summary = "Failed to decode image from ${file.name}"
                )
            }

            // Downscale if bitmap is huge (for 3GB RAM device optimization)
            val optimizedBitmap = downscaleIfNeeded(bitmap, 1280)
            val base64 = bitmapToBase64(optimizedBitmap)

            val prompt = buildVisionPrompt(taskType, userQuery)
            // Query model via orchestrator
            val resultText = orchestrator.generateVisionResponse(
                prompt = prompt,
                imageBase64 = base64
            )

            val parsedResult = VisionAnalysisResult(
                success = true,
                summary = resultText,
                rawText = if (taskType == VisionTaskType.OCR_EXTRACT) resultText else null,
                privacyBannerText = "Visual frame processed securely via Maya Vision Engine."
            )
            _lastAnalysisResult.value = parsedResult
            parsedResult
        } catch (e: Exception) {
            val errorResult = VisionAnalysisResult(
                success = false,
                summary = "Vision analysis failed: ${e.message}"
            )
            _lastAnalysisResult.value = errorResult
            errorResult
        } finally {
            _isProcessing.value = false
            _privacyStatus.value = null
        }
    }

    suspend fun analyzeBitmap(
        bitmap: Bitmap,
        taskType: VisionTaskType,
        userQuery: String? = null
    ): VisionAnalysisResult = withContext(Dispatchers.IO) {
        _isProcessing.value = true
        _privacyStatus.value = "Active: Analyzing screen capture with Maya Vision AI"

        try {
            val optimizedBitmap = downscaleIfNeeded(bitmap, 1280)
            val base64 = bitmapToBase64(optimizedBitmap)

            val prompt = buildVisionPrompt(taskType, userQuery)
            val resultText = orchestrator.generateVisionResponse(
                prompt = prompt,
                imageBase64 = base64
            )

            val result = VisionAnalysisResult(
                success = true,
                summary = resultText,
                privacyBannerText = "Screen capture frame processed securely."
            )
            _lastAnalysisResult.value = result
            result
        } catch (e: Exception) {
            val result = VisionAnalysisResult(
                success = false,
                summary = "Screen analysis failed: ${e.message}"
            )
            _lastAnalysisResult.value = result
            result
        } finally {
            _isProcessing.value = false
            _privacyStatus.value = null
        }
    }

    fun saveCapturedImage(bitmap: Bitmap, filenamePrefix: String = "maya_vision"): File {
        val dir = File(context.cacheDir, "vision_captures")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${filenamePrefix}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file
    }

    private fun downscaleIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (ratio > 1) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun buildVisionPrompt(taskType: VisionTaskType, userQuery: String?): String {
        val base = when (taskType) {
            VisionTaskType.OCR_EXTRACT ->
                "Perform accurate OCR on this image. Extract all text, signs, labels, or numbers verbatim. Maintain line breaks and format neatly."
            VisionTaskType.SCREENSHOT_ERROR_EXPLAIN ->
                "Analyze this screenshot. Identify what app is running, detect any error message, dialog, crash stack trace, or warning, and explain step-by-step how the user can fix or resolve it."
            VisionTaskType.DOCUMENT_SUMMARY ->
                "Analyze this document image. Identify the title, key sections, main points, and summarize the contents clearly with bullet points."
            VisionTaskType.OBJECT_DESCRIPTION ->
                "Describe the scene and key objects in this image clearly, noting spatial layout, colors, and notable details in an expressive, helpful tone."
            VisionTaskType.DIAGRAM_ANALYSIS ->
                "Analyze this diagram/chart. Explain its flow, entities, relationships, labels, and the conclusion it presents."
            VisionTaskType.TRANSLATION ->
                "Read any text in this image and translate it to Hindi and English with natural fluency."
        }
        return if (!userQuery.isNullOrEmpty()) {
            "$base\nUser query: $userQuery"
        } else {
            base
        }
    }
}
