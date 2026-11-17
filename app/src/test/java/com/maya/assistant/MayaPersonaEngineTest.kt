package com.maya.assistant

import com.maya.assistant.ai.MayaPersonaEngine
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.data.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MayaPersonaEngineTest {

    private val engine = MayaPersonaEngine()
    private val defaultSettings = UserSettings(userName = "dost")

    @Test
    fun testGreetingResponse() {
        val response = engine.generateResponse("Namaste Maya", defaultSettings)
        assertNotNull(response.text)
        assertTrue(response.text.contains("Maya"))
        assertTrue(response.text.contains("dost"))
    }

    @Test
    fun testOrbStatesDefined() {
        assertEquals(8, OrbState.values().size)
    }

    @Test
    fun testGirlfriendModeResponse() {
        val gfSettings = defaultSettings.copy(isGirlfriendMode = true)
        val response = engine.generateResponse("pyaar", gfSettings)
        assertTrue(response.text.contains("hamesha aapke saath hoon"))
    }
}
