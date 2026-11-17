package com.maya.assistant.telecom

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.maya.assistant.automation.ActionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class ContactMatch(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    val label: String? = null,
    val alias: String? = null
)

data class CallState(
    val state: String, // "IDLE", "RINGING", "OFFHOOK"
    val incomingNumber: String? = null,
    val callerName: String? = null
)

class PhoneCommunicationManager(private val context: Context) {

    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private val _callState = MutableStateFlow(CallState("IDLE"))
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    // In-memory contact aliases (e.g., Mom -> "Maa", Boss -> "Vikram Sir")
    private val contactAliases = mutableMapOf<String, String>()

    init {
        initCallStateListener()
    }

    fun setContactAlias(alias: String, actualName: String) {
        contactAliases[alias.trim().lowercase()] = actualName.trim()
    }

    fun removeContactAlias(alias: String) {
        contactAliases.remove(alias.trim().lowercase())
    }

    fun getContactAliases(): Map<String, String> = contactAliases.toMap()

    // Search contacts by query or alias
    suspend fun searchContacts(query: String): List<ContactMatch> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ContactMatch>()
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return@withContext results

        // Check if query is an alias
        val resolvedQuery = contactAliases[cleanQuery.lowercase()] ?: cleanQuery

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.LABEL
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$resolvedQuery%")

        try {
            val cursor: Cursor? = context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )
            cursor?.use {
                val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val labelIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)

                while (it.moveToNext()) {
                    val id = if (idIdx >= 0) it.getString(idIdx) else ""
                    val name = if (nameIdx >= 0) it.getString(nameIdx) else ""
                    val num = if (numIdx >= 0) it.getString(numIdx).replace("\\s+".toRegex(), "") else ""
                    val label = if (labelIdx >= 0) it.getString(labelIdx) else null

                    // Determine alias label if any
                    val matchingAlias = contactAliases.entries.firstOrNull { entry ->
                        entry.value.equals(name, ignoreCase = true)
                    }?.key

                    results.add(
                        ContactMatch(
                            id = id,
                            displayName = name,
                            phoneNumber = num,
                            label = label,
                            alias = matchingAlias
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted
        } catch (e: Exception) {
            // Content resolver error
        }
        results
    }

    // Call placement: Opens standard dialer with confirmation / number prefilled
    fun dialContact(phoneNumber: String): ActionResult {
        return try {
            val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Dialer opened for $phoneNumber")
        } catch (e: Exception) {
            ActionResult(false, "Failed to open dialer: ${e.message}")
        }
    }

    // Direct call placement when CALL_PHONE permission is available
    fun callContactDirect(phoneNumber: String): ActionResult {
        return try {
            val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$cleanNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(true, "Call initiated to $phoneNumber")
        } catch (e: SecurityException) {
            ActionResult(false, "CALL_PHONE permission not granted. Using dialer instead.")
        } catch (e: Exception) {
            ActionResult(false, "Failed to make call: ${e.message}")
        }
    }

    // WhatsApp Integration
    fun sendWhatsAppMessage(phoneNumber: String, message: String): ActionResult {
        return try {
            val cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val url = if (cleanNumber.isNotEmpty()) {
                "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"
            } else {
                "https://api.whatsapp.com/send?text=$encodedMessage"
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // Check if WhatsApp is installed, otherwise fallback to web browser
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                ActionResult(true, "WhatsApp conversation opened for preview & send confirmation.")
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                ActionResult(true, "WhatsApp Web opened in browser for preview & send confirmation.")
            }
        } catch (e: Exception) {
            ActionResult(false, "Failed to open WhatsApp: ${e.message}")
        }
    }

    fun shareMedia(uri: Uri, mimeType: String = "*/*", text: String? = null): ActionResult {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                if (!text.isNullOrEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share via Maya AI").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            ActionResult(true, "Share chooser opened.")
        } catch (e: Exception) {
            ActionResult(false, "Failed to share media: ${e.message}")
        }
    }

    private fun initCallStateListener() {
        val tm = telephonyManager ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mainExecutor = ContextCompat.getMainExecutor(context)
                tm.registerTelephonyCallback(
                    mainExecutor,
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            handleCallStateUpdate(state, null)
                        }
                    }
                )
            } else {
                @Suppress("DEPRECATION")
                tm.listen(object : PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        handleCallStateUpdate(state, phoneNumber)
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE)
            }
        } catch (e: SecurityException) {
            // Permission READ_PHONE_STATE not granted yet
        } catch (e: Exception) {
            // Telephony listener error
        }
    }

    private fun handleCallStateUpdate(state: Int, incomingNumber: String?) {
        val stateName = when (state) {
            TelephonyManager.CALL_STATE_RINGING -> "RINGING"
            TelephonyManager.CALL_STATE_OFFHOOK -> "OFFHOOK"
            else -> "IDLE"
        }
        _callState.value = CallState(
            state = stateName,
            incomingNumber = incomingNumber,
            callerName = incomingNumber?.let { num ->
                contactAliases[num] ?: num
            }
        )
    }
}
