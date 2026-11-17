package com.maya.assistant.data.model

data class UserSettings(
    val userName: String = "dost",
    val greetingPrefix: String = "Good evening,",
    val energyBalance: Int = 1,
    val freeMinutesRemaining: Int = 10,
    val isLicensed: Boolean = false,
    
    // Voice & Language
    val voiceLanguage: String = "hi-IN", // "hi-IN", "en-IN", "en-US"
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.05f, // Slightly higher pitch for warm natural female voice
    val voiceEngineMode: String = "Normal", // "Normal" vs "Advanced Agentic"
    val isWakeWordEnabled: Boolean = true,
    val wakeWord: String = "Hey Maya",
    
    // Personality
    val personaTone: String = "Friendly & Caring",
    val languageMix: String = "Hinglish", // "Hindi", "English", "Hinglish"
    val isGirlfriendMode: Boolean = false,
    val isRomanticMode: Boolean = false,
    
    // UI & Appearance
    val isDarkTheme: Boolean = false,
    val orbGlowIntensity: Float = 1.0f,
    val isEchoGuardEnabled: Boolean = true,
    val isVoiceGuardianEnabled: Boolean = true
)
