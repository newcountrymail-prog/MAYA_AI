package com.maya.assistant.ai.image

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class GeneratedArt(
    val prompt: String,
    val enhancedPrompt: String,
    val imageUrl: String,
    val aspectRatio: String = "1:1",
    val style: String = "Cinematic"
)

enum class ImageEditOperation {
    BackgroundRemoval,
    Upscaling,
    StyleTransformation,
    ImageVariation,
    Enhancement
}

class ImageStudioEngine {

    fun enhancePrompt(basePrompt: String, style: String = "Cinematic"): String {
        return "$basePrompt, $style lighting, highly detailed, 8k resolution, award-winning photography, photorealistic masterpiece"
    }

    fun generateImageUrl(
        prompt: String,
        aspectRatio: String = "1:1",
        style: String = "Cinematic"
    ): GeneratedArt {
        val enhanced = enhancePrompt(prompt, style)
        val encodedPrompt = URLEncoder.encode(enhanced, StandardCharsets.UTF_8.toString())

        val (width, height) = when (aspectRatio) {
            "16:9" -> Pair(1280, 720)
            "9:16" -> Pair(720, 1280)
            "4:3" -> Pair(1024, 768)
            else -> Pair(1024, 1024)
        }

        // Direct verified free image generator endpoint
        val url = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&seed=${System.currentTimeMillis() % 10000}&nologo=true"

        return GeneratedArt(
            prompt = prompt,
            enhancedPrompt = enhanced,
            imageUrl = url,
            aspectRatio = aspectRatio,
            style = style
        )
    }

    fun applyEditOperation(
        sourceUrl: String,
        operation: ImageEditOperation
    ): String {
        return when (operation) {
            ImageEditOperation.BackgroundRemoval -> "$sourceUrl&enhance=true&transparent=true"
            ImageEditOperation.Upscaling -> "$sourceUrl&width=2048&height=2048"
            ImageEditOperation.StyleTransformation -> "$sourceUrl&style=anime"
            ImageEditOperation.ImageVariation -> "$sourceUrl&seed=${(System.currentTimeMillis() + 100) % 10000}"
            ImageEditOperation.Enhancement -> "$sourceUrl&enhance=true"
        }
    }
}
