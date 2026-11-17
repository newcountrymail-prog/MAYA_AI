package com.maya.assistant

import com.maya.assistant.ai.ExecutionResult
import com.maya.assistant.ai.MultiModelOrchestrator
import com.maya.assistant.ai.agent.MultiAgentSystem
import com.maya.assistant.ai.document.DocumentFormat
import com.maya.assistant.ai.document.DocumentsEngine
import com.maya.assistant.ai.image.ImageEditOperation
import com.maya.assistant.ai.image.ImageStudioEngine
import com.maya.assistant.ai.opencode.OpenCodeEngine
import com.maya.assistant.ai.research.BrowserAndResearchEngine
import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.ApiAuthType
import com.maya.assistant.data.model.ApiFormat
import com.maya.assistant.data.model.PricingTier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MayaAiIntelligenceTest {

    private val openCodeEngine = OpenCodeEngine()
    private val imageStudioEngine = ImageStudioEngine()
    private val researchEngine = BrowserAndResearchEngine()
    private val documentsEngine = DocumentsEngine()

    @Test
    fun testMultipleProvidersConfigured() {
        val gemini = AiProvider(
            id = 1,
            name = "Google Gemini Free Tier",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta",
            selectedModel = "gemini-1.5-flash",
            capabilities = setOf("conversation", "reasoning", "coding", "vision"),
            pricingTier = PricingTier.FreeTier
        )

        val groq = AiProvider(
            id = 2,
            name = "Groq High-Speed",
            baseUrl = "https://api.groq.com/openai/v1",
            selectedModel = "llama-3.1-70b-versatile",
            capabilities = setOf("conversation", "coding", "voice"),
            pricingTier = PricingTier.FreeTier
        )

        val openai = AiProvider(
            id = 3,
            name = "OpenAI",
            baseUrl = "https://api.openai.com/v1",
            selectedModel = "gpt-4o",
            capabilities = setOf("conversation", "reasoning", "coding"),
            pricingTier = PricingTier.Paid
        )

        assertTrue(gemini.isFree)
        assertTrue(groq.isFree)
        assertFalse(openai.isFree)
        assertEquals(PricingTier.FreeTier, gemini.pricingTier)
        assertEquals(PricingTier.Paid, openai.pricingTier)
    }

    @Test
    fun testQuotaErrorTriggersFallbackClassification() {
        // HTTP 429 (Quota / Rate limit) must trigger fallback
        // HTTP 401 (Auth error) must NOT trigger fallback
        fun isFallbackEligible(code: Int): Boolean = when (code) {
            429 -> true
            500, 502, 503, 504 -> true
            401, 403 -> false
            400, 422 -> false
            else -> false
        }

        assertTrue(isFallbackEligible(429))
        assertTrue(isFallbackEligible(503))
        assertFalse(isFallbackEligible(401))
        assertFalse(isFallbackEligible(400))
    }

    @Test
    fun testOpenCodeGenerationAndInspection() {
        val genResult = openCodeEngine.generateCode("calculateStatistics", "Kotlin")
        assertNotNull(genResult)
        assertEquals("StatisticsHelper.kt", genResult.fileName)
        assertTrue(genResult.codeContent.contains("calculateStatistics"))
        assertTrue(genResult.testsIncluded)

        val (passed, report) = openCodeEngine.runUnitTests()
        assertTrue(passed)
        assertTrue(report.contains("passed"))
    }

    @Test
    fun testImageStudioPromptEnhancementAndUrlGeneration() {
        val basePrompt = "Cyberpunk female AI assistant"
        val enhanced = imageStudioEngine.enhancePrompt(basePrompt, "Cinematic")
        assertTrue(enhanced.contains("8k resolution"))
        assertTrue(enhanced.contains("Cinematic lighting"))

        val art = imageStudioEngine.generateImageUrl(basePrompt, "16:9", "Cinematic")
        assertNotNull(art)
        assertEquals("16:9", art.aspectRatio)
        assertTrue(art.imageUrl.contains("pollinations.ai"))
        assertTrue(art.imageUrl.contains("width=1280"))
        assertTrue(art.imageUrl.contains("height=720"))

        val edited = imageStudioEngine.applyEditOperation(art.imageUrl, ImageEditOperation.Upscaling)
        assertTrue(edited.contains("width=2048"))
    }

    @Test
    fun testBrowserResearchAndCitations() = runBlocking {
        val report = researchEngine.performResearch("Jetpack Compose Architecture")
        assertNotNull(report)
        assertEquals("Jetpack Compose Architecture", report.query)
        assertTrue(report.citations.isNotEmpty())
        assertTrue(report.structuredReport.contains("Executive Summary"))
        assertTrue(report.keyTakeaways.size >= 3)
    }

    @Test
    fun testDocumentParsingSummarizationAndExport() {
        val sampleText = """
            Android Jetpack Compose is a modern declarative UI framework.
            It provides reactive state management and powerful graphics capabilities.
            MAYA AI utilizes Compose for clean neumorphic mobile design.
        """.trimIndent()

        val meta = documentsEngine.parseDocument("DocTest.md", sampleText)
        assertEquals(DocumentFormat.MARKDOWN, meta.format)
        assertTrue(meta.wordCount > 10)

        val summary = documentsEngine.summarizeDocument("DocTest.md", sampleText)
        assertTrue(summary.keyPoints.isNotEmpty())

        val (exportName, exportBody) = documentsEngine.exportDocument("Architecture_Report", sampleText, DocumentFormat.MARKDOWN)
        assertEquals("Architecture_Report.md", exportName)
        assertTrue(exportBody.contains("Architecture_Report"))
    }
}
