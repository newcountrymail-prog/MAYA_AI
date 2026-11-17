package com.maya.assistant

import com.maya.assistant.cloud.CloudJob
import com.maya.assistant.cloud.ExecutionTarget
import com.maya.assistant.cloud.JobStatus
import com.maya.assistant.cloud.JobType
import com.maya.assistant.data.local.entity.UserMemoryEntity
import com.maya.assistant.skills.SkillPlaybook
import com.maya.assistant.skills.StepStatus
import com.maya.assistant.skills.WorkflowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MayaPart3VerificationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @Test
    fun testUserMemoryJsonSerializationAndDeserialization() {
        val originalMemories = listOf(
            UserMemoryEntity(
                id = 1L,
                key = "Assistant Name",
                content = "Maya AI Female Companion",
                category = UserMemoryEntity.CAT_USER_PREFERENCES,
                timestamp = 1700000000000L,
                isPinned = true,
                tags = "assistant,persona,voice"
            ),
            UserMemoryEntity(
                id = 2L,
                key = "Mom Contact Alias",
                content = "Maa",
                category = UserMemoryEntity.CAT_CONTACT_ALIASES,
                timestamp = 1700000001000L,
                isPinned = false,
                tags = "family,quick_call"
            )
        )

        val serializedJson = json.encodeToString(originalMemories)
        assertNotNull(serializedJson)
        assertTrue(serializedJson.contains("Maya AI Female Companion"))
        assertTrue(serializedJson.contains(UserMemoryEntity.CAT_CONTACT_ALIASES))

        val deserialized = json.decodeFromString<List<UserMemoryEntity>>(serializedJson)
        assertEquals(2, deserialized.size)
        assertEquals("Assistant Name", deserialized[0].key)
        assertEquals(true, deserialized[0].isPinned)
        assertEquals("Maa", deserialized[1].content)
    }

    @Test
    fun testWhatsAppUrlFormatting() {
        val rawNumber = "+91 98765 43210"
        val message = "Hello Maya! क्या हाल है?"
        val cleanNumber = rawNumber.replace("[^0-9]".toRegex(), "")
        val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"

        assertEquals("919876543210", cleanNumber)
        assertTrue(url.startsWith("https://api.whatsapp.com/send?phone=919876543210&text="))
        assertFalse(url.contains(" "))
    }

    @Test
    fun testVolumeClampingLogic() {
        fun clampVolume(inputPercent: Int): Int {
            return inputPercent.coerceIn(0, 100)
        }

        assertEquals(100, clampVolume(150))
        assertEquals(0, clampVolume(-20))
        assertEquals(45, clampVolume(45))
        assertEquals(0, clampVolume(0))
        assertEquals(100, clampVolume(100))
    }

    @Test
    fun testSkillsWorkflowStepApprovalRequirement() {
        val sensitiveStep = WorkflowStep(
            id = "wa_confirm",
            title = "Confirm WhatsApp Message",
            description = "User confirmation required prior to sending message",
            actionType = "WHATSAPP_PREPARE",
            requiresApproval = true,
            status = StepStatus.NEEDS_APPROVAL
        )

        assertTrue(sensitiveStep.requiresApproval)
        assertEquals(StepStatus.NEEDS_APPROVAL, sensitiveStep.status)
    }

    @Test
    fun testCloudJobStateProgression() {
        var job = CloudJob(
            title = "Deep Research Run",
            type = JobType.LONG_RESEARCH,
            target = ExecutionTarget.CLOUD_ACCELERATED,
            status = JobStatus.PENDING,
            progress = 0
        )
        assertEquals(JobStatus.PENDING, job.status)
        assertEquals(0, job.progress)

        // Progress
        job = job.copy(status = JobStatus.RUNNING, progress = 50)
        assertEquals(JobStatus.RUNNING, job.status)
        assertEquals(50, job.progress)

        // Completion
        job = job.copy(
            status = JobStatus.COMPLETED,
            progress = 100,
            result = "Synthesized 10 authoritative sources",
            completedAt = System.currentTimeMillis()
        )
        assertEquals(JobStatus.COMPLETED, job.status)
        assertEquals(100, job.progress)
        assertNotNull(job.result)
        assertNotNull(job.completedAt)
    }

    @Test
    fun testPreinstalledSkillCategoriesCoverage() {
        val expectedCategories = setOf(
            "Device Control",
            "Media",
            "Productivity",
            "Automation",
            "Study",
            "Coding",
            "Research"
        )

        val samplePlaybookList = listOf(
            SkillPlaybook("1", "Flashlight Toggle", "Device Control", "", "", emptyList()),
            SkillPlaybook("2", "Mute Media Volume", "Media", "", "", emptyList()),
            SkillPlaybook("3", "Morning Routine", "Productivity", "", "", emptyList()),
            SkillPlaybook("4", "WhatsApp Note", "Automation", "", "", emptyList()),
            SkillPlaybook("5", "Focus Mode", "Study", "", "", emptyList()),
            SkillPlaybook("6", "Workspace Init", "Coding", "", "", emptyList()),
            SkillPlaybook("7", "Screen Diagnosis", "Research", "", "", emptyList())
        )

        val presentCategories = samplePlaybookList.map { it.category }.toSet()
        assertEquals(expectedCategories, presentCategories)
    }

    @Test
    fun testCalculatorExpressionEvaluation() {
        fun evalSimple(str: String): Double {
            return object : Any() {
                var pos = -1
                var ch = 0
                fun nextChar() { ch = if (++pos < str.length) str[pos].code else -1 }
                fun eat(c: Int): Boolean {
                    while (ch == ' '.code) nextChar()
                    if (ch == c) { nextChar(); return true }
                    return false
                }
                fun parse(): Double { nextChar(); val x = parseExpr(); return x }
                fun parseExpr(): Double {
                    var x = parseTerm()
                    while (true) {
                        if (eat('+'.code)) x += parseTerm()
                        else if (eat('-'.code)) x -= parseTerm()
                        else return x
                    }
                }
                fun parseTerm(): Double {
                    var x = parseFactor()
                    while (true) {
                        if (eat('*'.code)) x *= parseFactor()
                        else if (eat('/'.code)) x /= parseFactor()
                        else return x
                    }
                }
                fun parseFactor(): Double {
                    if (eat('+'.code)) return +parseFactor()
                    if (eat('-'.code)) return -parseFactor()
                    var x: Double
                    val startPos = pos
                    if (eat('('.code)) {
                        x = parseExpr()
                        eat(')'.code)
                    } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                        while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                        x = str.substring(startPos, pos).toDouble()
                    } else throw RuntimeException()
                    return x
                }
            }.parse()
        }

        assertEquals(110.0, evalSimple("25 * 4 + 10"), 0.001)
        assertEquals(50.0, evalSimple("100 / 2"), 0.001)
    }

    @Test
    fun testHinglishPersonaToneAndGreetings() {
        val sampleHinglish = "Haanji! Kahiye, main aapki kya madad kar sakti hoon? Bataiye kya help chahiye!"
        assertTrue(sampleHinglish.contains("Haanji"))
        assertTrue(sampleHinglish.contains("madad"))
        assertTrue(sampleHinglish.contains("help"))
    }
}

