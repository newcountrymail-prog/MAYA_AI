package com.maya.assistant.ui.screens.automation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.automation.ActionResult
import com.maya.assistant.automation.InstalledAppInfo
import com.maya.assistant.telecom.ContactMatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AutomationViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val phoneManager = mayaApp.phoneControlManager
    private val telecomManager = mayaApp.phoneCommunicationManager

    private val _isTorchOn = MutableStateFlow(phoneManager.isTorchActive())
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private val _volumePercent = MutableStateFlow(phoneManager.getMediaVolumePercent())
    val volumePercent: StateFlow<Int> = _volumePercent.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _matchedContacts = MutableStateFlow<List<ContactMatch>>(emptyList())
    val matchedContacts: StateFlow<List<ContactMatch>> = _matchedContacts.asStateFlow()

    private val _lastActionStatus = MutableStateFlow<String?>("Ready for phone control actions")
    val lastActionStatus: StateFlow<String?> = _lastActionStatus.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(phoneManager.isAccessibilityActive())
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    init {
        refreshState()
    }

    fun refreshState() {
        _isTorchOn.value = phoneManager.isTorchActive()
        _volumePercent.value = phoneManager.getMediaVolumePercent()
        _isAccessibilityEnabled.value = phoneManager.isAccessibilityActive()
        viewModelScope.launch {
            _installedApps.value = phoneManager.getInstalledApps(includeSystem = false)
        }
    }

    fun toggleTorch() {
        val result = phoneManager.toggleTorch()
        _isTorchOn.value = phoneManager.isTorchActive()
        _lastActionStatus.value = result.message
    }

    fun setVolume(percent: Int) {
        val result = phoneManager.setMediaVolumePercent(percent)
        _volumePercent.value = phoneManager.getMediaVolumePercent()
        _lastActionStatus.value = result.message
    }

    fun adjustVolumeUp() {
        val res = phoneManager.adjustVolumeUp()
        _volumePercent.value = phoneManager.getMediaVolumePercent()
        _lastActionStatus.value = res.message
    }

    fun adjustVolumeDown() {
        val res = phoneManager.adjustVolumeDown()
        _volumePercent.value = phoneManager.getMediaVolumePercent()
        _lastActionStatus.value = res.message
    }

    fun mediaPlayPause() {
        val res = phoneManager.togglePlayPause()
        _lastActionStatus.value = res.message
    }

    fun mediaNext() {
        val res = phoneManager.nextTrack()
        _lastActionStatus.value = res.message
    }

    fun mediaPrev() {
        val res = phoneManager.previousTrack()
        _lastActionStatus.value = res.message
    }

    fun launchApp(pkg: String) {
        val res = phoneManager.launchAppByPackage(pkg)
        _lastActionStatus.value = res.message
    }

    fun setAlarm(hour: Int, minute: Int, message: String) {
        val res = phoneManager.setAlarm(hour, minute, message)
        _lastActionStatus.value = res.message
    }

    fun setTimer(seconds: Int, message: String) {
        val res = phoneManager.setTimer(seconds, message)
        _lastActionStatus.value = res.message
    }

    fun createCalendarEvent(title: String, desc: String) {
        val res = phoneManager.createCalendarEvent(title, desc)
        _lastActionStatus.value = res.message
    }

    fun openAccessibilitySettings() {
        phoneManager.openAccessibilitySettings()
    }

    fun openNotificationSettings() {
        phoneManager.openNotificationSettings()
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            _matchedContacts.value = telecomManager.searchContacts(query)
        }
    }

    fun dialContact(phoneNumber: String) {
        val res = telecomManager.dialContact(phoneNumber)
        _lastActionStatus.value = res.message
    }

    fun sendWhatsApp(phoneNumber: String, text: String) {
        val res = telecomManager.sendWhatsAppMessage(phoneNumber, text)
        _lastActionStatus.value = res.message
    }
}
