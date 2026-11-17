package com.maya.assistant.automation

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.concurrent.CopyOnWriteArrayList

data class PermittedNotification(
    val id: Int,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long
)

class MayaNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        currentInstance = this
        isListenerConnected = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        if (currentInstance == this) {
            currentInstance = null
            isListenerConnected = false
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isNotEmpty() || text.isNotEmpty()) {
            val item = PermittedNotification(
                id = sbn.id,
                packageName = sbn.packageName,
                title = title,
                text = text,
                postTime = sbn.postTime
            )
            recentNotifications.add(0, item)
            // Keep buffer reasonable to save memory
            while (recentNotifications.size > 50) {
                recentNotifications.removeAt(recentNotifications.lastIndex)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn ?: return
        recentNotifications.removeAll { it.id == sbn.id && it.packageName == sbn.packageName }
    }

    companion object {
        var currentInstance: MayaNotificationListenerService? = null
            private set
        var isListenerConnected = false
            private set

        private val recentNotifications = CopyOnWriteArrayList<PermittedNotification>()

        fun getRecentNotifications(): List<PermittedNotification> = recentNotifications.toList()

        fun clearNotifications() {
            recentNotifications.clear()
        }
    }
}
