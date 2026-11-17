package com.maya.assistant.tools

import android.content.Context
import com.maya.assistant.automation.ActionResult
import com.maya.assistant.automation.PhoneControlManager
import com.maya.assistant.data.local.entity.UserMemoryEntity
import com.maya.assistant.data.repository.UserMemoryRepository
import com.maya.assistant.telecom.PhoneCommunicationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

data class ToolExecutionResult(
    val toolName: String,
    val success: Boolean,
    val output: String,
    val speechResponse: String
)

class MayaToolsManager(
    private val context: Context,
    private val phoneControlManager: PhoneControlManager,
    private val phoneCommunicationManager: PhoneCommunicationManager,
    private val userMemoryRepository: UserMemoryRepository
) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 1. Live Weather Tool
    suspend fun getLiveWeather(location: String = ""): ToolExecutionResult = withContext(Dispatchers.IO) {
        try {
            val url = if (location.isNotBlank()) {
                val encoded = URLEncoder.encode(location, StandardCharsets.UTF_8.toString())
                "https://wttr.in/$encoded?format=j1"
            } else {
                "https://wttr.in/?format=j1"
            }

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MayaAI/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val currentCond = json.getJSONArray("current_condition").getJSONObject(0)
                val tempC = currentCond.getString("temp_C")
                val weatherDesc = currentCond.getJSONArray("weatherDesc").getJSONObject(0).getString("value")
                val humidity = currentCond.getString("humidity")
                val windKmph = currentCond.getString("windspeedKmph")

                val areaName = try {
                    json.getJSONArray("nearest_area").getJSONObject(0).getJSONArray("areaName").getJSONObject(0).getString("value")
                } catch (e: Exception) {
                    if (location.isNotBlank()) location else "Aapke shahar"
                }

                val speech = "$areaName me abhi temperature $tempC degree Celsius hai, mausam $weatherDesc hai aur humidity $humidity% hai."
                val output = "Weather in $areaName: $tempC°C, $weatherDesc (Humidity: $humidity%, Wind: $windKmph km/h)"
                ToolExecutionResult("Live Weather", true, output, speech)
            } else {
                ToolExecutionResult("Live Weather", false, "Weather service unavailable (HTTP ${response.code})", "Mausam ki jankari abhi fetch nahi ho payi.")
            }
        } catch (e: Exception) {
            ToolExecutionResult("Live Weather", false, "Weather error: ${e.message}", "Network issue ki wajah se mausam check nahi ho saka.")
        }
    }

    // 2. Real Web Search Tool
    suspend fun searchWeb(query: String): ToolExecutionResult = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val url = "https://api.duckduckgo.com/?q=$encodedQuery&format=json&no_html=1&skip_disambig=1"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MayaAI/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val abstractText = json.optString("AbstractText", "")
                val heading = json.optString("Heading", query)

                if (abstractText.isNotBlank()) {
                    val speech = "$heading ke baare me: $abstractText"
                    ToolExecutionResult("Web Search", true, abstractText, speech)
                } else {
                    // Fallback to related topic if abstract is empty
                    val related = json.optJSONArray("RelatedTopics")
                    if (related != null && related.length() > 0) {
                        val first = related.getJSONObject(0).optString("Text", "")
                        ToolExecutionResult("Web Search", true, first, first)
                    } else {
                        ToolExecutionResult("Web Search", true, "Results found for '$query'", "Maine '$query' ke baare me web par check kiya hai.")
                    }
                }
            } else {
                ToolExecutionResult("Web Search", false, "Search failed", "Search result fetch nahi ho saka.")
            }
        } catch (e: Exception) {
            ToolExecutionResult("Web Search", false, "Error: ${e.message}", "Web search me dikkat aayi.")
        }
    }

    // 3. Calculator & Unit Converter
    fun calculateExpression(expr: String): ToolExecutionResult {
        return try {
            val cleanExpr = expr.replace("x", "*").replace("X", "*").replace(" ", "")
            // Safe evaluation of basic math expressions
            val result = evalSimple(cleanExpr)
            val speech = "Iska answer $result hai."
            ToolExecutionResult("Calculator", true, "$cleanExpr = $result", speech)
        } catch (e: Exception) {
            ToolExecutionResult("Calculator", false, "Could not calculate: $expr", "Calculation me problem aayi.")
        }
    }

    private fun evalSimple(str: String): Double {
        // Quick recursive descent parser for basic math (+, -, *, /)
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
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
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }

    // 4. Smart Memory Saver
    suspend fun saveMemoryFromSpeech(key: String, content: String): ToolExecutionResult {
        return try {
            userMemoryRepository.addMemory(
                key = key,
                content = content,
                category = UserMemoryEntity.CAT_IMPORTANT_NOTES,
                isPinned = true,
                tags = "voice_saved,note"
            )
            ToolExecutionResult(
                toolName = "Memory Saver",
                success = true,
                output = "Saved note: '$key' -> '$content'",
                speechResponse = "Ji bilkul! Maine yaad rakh liya hai ki $key: $content."
            )
        } catch (e: Exception) {
            ToolExecutionResult(
                toolName = "Memory Saver",
                success = false,
                output = "Memory save failed: ${e.message}",
                speechResponse = "Sorry, memory save karte waqt issue aaya."
            )
        }
    }

    // 5. Phone Controls (Torch, Volume, Alarm, Apps)
    fun toggleTorch(): ToolExecutionResult {
        val res = phoneControlManager.toggleTorch()
        val speech = if (phoneControlManager.isTorchActive()) "Torch on kar di hai." else "Torch off kar di hai."
        return ToolExecutionResult("Flashlight", res.success, res.message, speech)
    }

    fun setVolume(percent: Int): ToolExecutionResult {
        val res = phoneControlManager.setMediaVolumePercent(percent)
        val speech = "Media volume $percent% par set kar diya hai."
        return ToolExecutionResult("Volume", res.success, res.message, speech)
    }

    fun setAlarm(hour: Int, minute: Int, message: String): ToolExecutionResult {
        val res = phoneControlManager.setAlarm(hour, minute, message)
        val speech = "Theek hai! Subah $hour baj kar $minute minute par alarm set kar diya hai."
        return ToolExecutionResult("Alarm", res.success, res.message, speech)
    }

    fun setTimer(seconds: Int, message: String): ToolExecutionResult {
        val res = phoneControlManager.setTimer(seconds, message)
        val minutes = seconds / 60
        val speech = if (minutes > 0) "$minutes minute ka timer set kar diya hai." else "$seconds second ka timer start kar diya hai."
        return ToolExecutionResult("Timer", res.success, res.message, speech)
    }

    suspend fun launchApp(appName: String): ToolExecutionResult {
        val res = phoneControlManager.launchAppByName(appName)
        val speech = if (res.success) "$appName open kar rahi hoon." else "$appName phone me nahi mila."
        return ToolExecutionResult("App Launcher", res.success, res.message, speech)
    }

    // 6. WhatsApp Direct Message
    fun sendWhatsApp(phoneNumber: String, message: String): ToolExecutionResult {
        val res = phoneCommunicationManager.sendWhatsAppMessage(phoneNumber, message)
        val speech = "WhatsApp open kar diya hai, aap preview karke send press kar sakte hain."
        return ToolExecutionResult("WhatsApp", res.success, res.message, speech)
    }
}
