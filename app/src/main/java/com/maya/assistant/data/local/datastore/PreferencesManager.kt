package com.maya.assistant.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.maya.assistant.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maya_user_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val GREETING_PREFIX = stringPreferencesKey("greeting_prefix")
        val ENERGY_BALANCE = intPreferencesKey("energy_balance")
        val FREE_MINUTES = intPreferencesKey("free_minutes")
        val IS_LICENSED = booleanPreferencesKey("is_licensed")
        
        val VOICE_LANGUAGE = stringPreferencesKey("voice_language")
        val VOICE_SPEED = floatPreferencesKey("voice_speed")
        val VOICE_PITCH = floatPreferencesKey("voice_pitch")
        val VOICE_ENGINE_MODE = stringPreferencesKey("voice_engine_mode")
        val IS_WAKE_WORD_ENABLED = booleanPreferencesKey("is_wake_word_enabled")
        
        val PERSONA_TONE = stringPreferencesKey("persona_tone")
        val LANGUAGE_MIX = stringPreferencesKey("language_mix")
        val IS_GIRLFRIEND_MODE = booleanPreferencesKey("is_girlfriend_mode")
        val IS_ROMANTIC_MODE = booleanPreferencesKey("is_romantic_mode")
        
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val ORB_GLOW_INTENSITY = floatPreferencesKey("orb_glow_intensity")
        val IS_ECHO_GUARD = booleanPreferencesKey("is_echo_guard")
        val IS_VOICE_GUARDIAN = booleanPreferencesKey("is_voice_guardian")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            userName = prefs[Keys.USER_NAME] ?: "dost",
            greetingPrefix = prefs[Keys.GREETING_PREFIX] ?: "Good evening,",
            energyBalance = prefs[Keys.ENERGY_BALANCE] ?: 1,
            freeMinutesRemaining = prefs[Keys.FREE_MINUTES] ?: 10,
            isLicensed = prefs[Keys.IS_LICENSED] ?: false,
            
            voiceLanguage = prefs[Keys.VOICE_LANGUAGE] ?: "hi-IN",
            voiceSpeed = prefs[Keys.VOICE_SPEED] ?: 1.0f,
            voicePitch = prefs[Keys.VOICE_PITCH] ?: 1.05f,
            voiceEngineMode = prefs[Keys.VOICE_ENGINE_MODE] ?: "Normal",
            isWakeWordEnabled = prefs[Keys.IS_WAKE_WORD_ENABLED] ?: true,
            
            personaTone = prefs[Keys.PERSONA_TONE] ?: "Friendly & Caring",
            languageMix = prefs[Keys.LANGUAGE_MIX] ?: "Hinglish",
            isGirlfriendMode = prefs[Keys.IS_GIRLFRIEND_MODE] ?: false,
            isRomanticMode = prefs[Keys.IS_ROMANTIC_MODE] ?: false,
            
            isDarkTheme = prefs[Keys.IS_DARK_THEME] ?: false,
            orbGlowIntensity = prefs[Keys.ORB_GLOW_INTENSITY] ?: 1.0f,
            isEchoGuardEnabled = prefs[Keys.IS_ECHO_GUARD] ?: true,
            isVoiceGuardianEnabled = prefs[Keys.IS_VOICE_GUARDIAN] ?: true
        )
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = name }
    }

    suspend fun updateVoiceLanguage(lang: String) {
        context.dataStore.edit { it[Keys.VOICE_LANGUAGE] = lang }
    }

    suspend fun updateVoiceSpeed(speed: Float) {
        context.dataStore.edit { it[Keys.VOICE_SPEED] = speed }
    }

    suspend fun updateVoicePitch(pitch: Float) {
        context.dataStore.edit { it[Keys.VOICE_PITCH] = pitch }
    }

    suspend fun updateVoiceEngineMode(mode: String) {
        context.dataStore.edit { it[Keys.VOICE_ENGINE_MODE] = mode }
    }

    suspend fun updateTheme(isDark: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_THEME] = isDark }
    }

    suspend fun updateGirlfriendMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_GIRLFRIEND_MODE] = enabled }
    }
}
