package com.maya.assistant.automation

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.view.KeyEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean
)

data class ActionResult(
    val success: Boolean,
    val message: String,
    val details: String? = null
)

class PhoneControlManager(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var isTorchOn: Boolean = false

    // App Control
    suspend fun getInstalledApps(includeSystem: Boolean = false): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<InstalledAppInfo>()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in packages) {
            val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (includeSystem || !isSystem) {
                val name = packageManager.getApplicationLabel(app).toString()
                apps.add(InstalledAppInfo(appName = name, packageName = app.packageName, isSystemApp = isSystem))
            }
        }
        apps.sortedBy { it.appName.lowercase() }
    }

    suspend fun searchApps(query: String): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val all = getInstalledApps(includeSystem = true)
        all.filter { it.appName.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
    }

    fun launchAppByPackage(packageName: String): ActionResult {
        return try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                ActionResult(true, "App launched: $packageName")
            } else {
                ActionResult(false, "Could not create launch intent for $packageName")
            }
        } catch (e: Exception) {
            ActionResult(false, "Failed to launch app: ${e.message}")
        }
    }

    suspend fun launchAppByName(name: String): ActionResult {
        val matches = searchApps(name)
        if (matches.isEmpty()) {
            return ActionResult(false, "No installed app found matching '$name'")
        }
        val bestMatch = matches.first()
        return launchAppByPackage(bestMatch.packageName)
    }

    // Torch / Flashlight Control
    fun toggleTorch(): ActionResult {
        return setTorch(!isTorchOn)
    }

    fun setTorch(enable: Boolean): ActionResult {
        if (cameraManager == null) {
            return ActionResult(false, "CameraManager unavailable on this device")
        }
        return try {
            val cameraIds = cameraManager.cameraIdList
            var torchCameraId: String? = null
            for (id in cameraIds) {
                val chars = cameraManager.getCameraCharacteristics(id)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = chars.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    torchCameraId = id
                    break
                }
            }
            if (torchCameraId != null) {
                cameraManager.setTorchMode(torchCameraId, enable)
                isTorchOn = enable
                ActionResult(true, if (enable) "Torch turned ON" else "Torch turned OFF")
            } else {
                ActionResult(false, "No rear camera with flash found")
            }
        } catch (e: CameraAccessException) {
            ActionResult(false, "Camera access error: ${e.message}")
        } catch (e: Exception) {
            ActionResult(false, "Failed to toggle torch: ${e.message}")
        }
    }

    fun isTorchActive(): Boolean = isTorchOn

    // Volume Controls
    fun setMediaVolumePercent(percent: Int): ActionResult {
        val audio = audioManager ?: return ActionResult(false, "AudioManager unavailable")
        return try {
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val clamped = percent.coerceIn(0, 100)
            val target = (maxVolume * clamped) / 100
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, target, AudioManager.FLAG_SHOW_UI)
            ActionResult(true, "Media volume set to $clamped% (level $target/$maxVolume)")
        } catch (e: Exception) {
            ActionResult(false, "Failed to set volume: ${e.message}")
        }
    }

    fun getMediaVolumePercent(): Int {
        val audio = audioManager ?: return 0
        val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (maxVolume == 0) return 0
        val current = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (current * 100) / maxVolume
    }

    fun adjustVolumeUp(): ActionResult {
        val audio = audioManager ?: return ActionResult(false, "AudioManager unavailable")
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        return ActionResult(true, "Volume raised")
    }

    fun adjustVolumeDown(): ActionResult {
        val audio = audioManager ?: return ActionResult(false, "AudioManager unavailable")
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        return ActionResult(true, "Volume lowered")
    }

    // Media Playback Controls
    fun sendMediaPlaybackKey(keyCode: Int): ActionResult {
        val audio = audioManager ?: return ActionResult(false, "AudioManager unavailable")
        return try {
            val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audio.dispatchMediaKeyEvent(downEvent)
            audio.dispatchMediaKeyEvent(upEvent)
            ActionResult(true, "Media key dispatched: $keyCode")
        } catch (e: Exception) {
            ActionResult(false, "Failed to dispatch media key: ${e.message}")
        }
    }

    fun togglePlayPause(): ActionResult = sendMediaPlaybackKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    fun nextTrack(): ActionResult = sendMediaPlaybackKey(KeyEvent.KEYCODE_MEDIA_NEXT)
    fun previousTrack(): ActionResult = sendMediaPlaybackKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)

    // Alarm & Timer
    fun setAlarm(hour: Int, minutes: Int, message: String = "Maya AI Alarm"): ActionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Alarm set for %02d:%02d ('$message')".format(hour, minutes))
        } catch (e: Exception) {
            ActionResult(false, "Failed to set alarm: ${e.message}")
        }
    }

    fun setTimer(seconds: Int, message: String = "Maya AI Timer"): ActionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Timer set for $seconds seconds ('$message')")
        } catch (e: Exception) {
            ActionResult(false, "Failed to set timer: ${e.message}")
        }
    }

    // Calendar Event
    fun createCalendarEvent(
        title: String,
        description: String = "",
        beginTimeMs: Long = System.currentTimeMillis() + 3600000,
        endTimeMs: Long = System.currentTimeMillis() + 7200000
    ): ActionResult {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMs)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMs)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Calendar event creation opened: $title")
        } catch (e: Exception) {
            ActionResult(false, "Failed to create calendar event: ${e.message}")
        }
    }

    // System Settings Shortcuts
    fun openSettings(action: String): ActionResult {
        return try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Opened settings: $action")
        } catch (e: Exception) {
            ActionResult(false, "Failed to open settings: ${e.message}")
        }
    }

    fun openWifiSettings(): ActionResult = openSettings(Settings.ACTION_WIFI_SETTINGS)
    fun openBluetoothSettings(): ActionResult = openSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
    fun openAccessibilitySettings(): ActionResult = openSettings(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    fun openSoundSettings(): ActionResult = openSettings(Settings.ACTION_SOUND_SETTINGS)
    fun openNotificationSettings(): ActionResult = openSettings(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

    // Accessibility Service Automation
    fun isAccessibilityActive(): Boolean = MayaAccessibilityService.isRunning()

    fun performTap(x: Float, y: Float, onComplete: ((Boolean) -> Unit)? = null): ActionResult {
        val service = MayaAccessibilityService.currentInstance
        return if (service != null) {
            service.performTap(x, y, onComplete)
            ActionResult(true, "Tap gesture dispatched at ($x, $y)")
        } else {
            ActionResult(false, "Accessibility Service is not enabled. Please enable MAYA AI in Accessibility Settings.")
        }
    }

    fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float, onComplete: ((Boolean) -> Unit)? = null): ActionResult {
        val service = MayaAccessibilityService.currentInstance
        return if (service != null) {
            service.performSwipe(startX, startY, endX, endY, onComplete = onComplete)
            ActionResult(true, "Swipe gesture dispatched from ($startX, $startY) to ($endX, $endY)")
        } else {
            ActionResult(false, "Accessibility Service is not enabled. Please enable MAYA AI in Accessibility Settings.")
        }
    }

    fun clickTextOnScreen(text: String): ActionResult {
        val service = MayaAccessibilityService.currentInstance
        return if (service != null) {
            val clicked = service.findAndClickText(text)
            if (clicked) {
                ActionResult(true, "Clicked text '$text' on screen")
            } else {
                ActionResult(false, "Text '$text' not found or not clickable on current screen")
            }
        } else {
            ActionResult(false, "Accessibility Service is not enabled.")
        }
    }

    fun readScreenText(): List<String> {
        val service = MayaAccessibilityService.currentInstance ?: return emptyList()
        return service.captureVisibleTextOnScreen()
    }
}
