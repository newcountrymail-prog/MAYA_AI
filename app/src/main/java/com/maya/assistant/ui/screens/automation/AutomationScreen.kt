package com.maya.assistant.ui.screens.automation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyanAccent
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun AutomationScreen(
    onNavigateBack: () -> Unit,
    viewModel: AutomationViewModel = viewModel()
) {
    val isTorchOn by viewModel.isTorchOn.collectAsState()
    val volumePercent by viewModel.volumePercent.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val matchedContacts by viewModel.matchedContacts.collectAsState()
    val actionStatus by viewModel.lastActionStatus.collectAsState()
    val isAccessibilityEnabled by viewModel.isAccessibilityEnabled.collectAsState()

    var contactQuery by remember { mutableStateOf("") }
    var whatsAppText by remember { mutableStateOf("Namaste from MAYA AI!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MayaNavy
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Phone Automation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaNavy
                )
                Text(
                    text = "Device Controls • Contacts • Media • Accessibility",
                    fontSize = 12.sp,
                    color = MayaTextSecondary
                )
            }
        }

        // Live status banner
        if (actionStatus != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0F2FE))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = actionStatus ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF0369A1),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hardware Quick Controls
            item {
                Text(
                    text = "Hardware Controls",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaNavy,
                    modifier = Modifier.padding(top = 8.dp)
                )

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Torch",
                                    tint = if (isTorchOn) Color(0xFFEAB308) else MayaTextSecondary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Camera Torch",
                                        fontWeight = FontWeight.SemiBold,
                                        color = MayaTextPrimary
                                    )
                                    Text(
                                        text = if (isTorchOn) "Status: ON" else "Status: OFF",
                                        fontSize = 12.sp,
                                        color = MayaTextSecondary
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.toggleTorch() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTorchOn) Color(0xFFEAB308) else MayaNavy
                                )
                            ) {
                                Text(if (isTorchOn) "Turn OFF" else "Turn ON")
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Volume Control
                        Text(
                            text = "Media Volume: $volumePercent%",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MayaTextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { viewModel.adjustVolumeDown() }) {
                                Icon(Icons.Default.VolumeDown, contentDescription = "Down", tint = MayaNavy)
                            }
                            Slider(
                                value = volumePercent.toFloat(),
                                onValueChange = { viewModel.setVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = MayaCyanAccent,
                                    activeTrackColor = MayaNavy
                                )
                            )
                            IconButton(onClick = { viewModel.adjustVolumeUp() }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Up", tint = MayaNavy)
                            }
                        }

                        // Media Playback Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.mediaPrev() }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = MayaNavy)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MayaNavy)
                                    .clickable { viewModel.mediaPlayPause() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play/Pause", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(onClick = { viewModel.mediaNext() }) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MayaNavy)
                            }
                        }
                    }
                }
            }

            // Quick Timers & Alarms
            item {
                Text(
                    text = "Alarms & Timers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaNavy
                )

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Quick Pomodoro Timers",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MayaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setTimer(300, "5 Min Focus") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                            ) {
                                Text("5 Min", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.setTimer(900, "15 Min Focus") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                            ) {
                                Text("15 Min", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.setTimer(1500, "25 Min Focus") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                            ) {
                                Text("25 Min", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setAlarm(7, 0, "Morning Briefing Alarm") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("7:00 AM Alarm", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.createCalendarEvent("Maya Planning Session", "Schedule & review with Maya") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                            ) {
                                Text("New Event", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // System Accessibility & Permissions
            item {
                Text(
                    text = "System Integrations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaNavy
                )

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = if (isAccessibilityEnabled) Color(0xFF16A34A) else MayaTextSecondary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Accessibility Automation",
                                        fontWeight = FontWeight.Medium,
                                        color = MayaTextPrimary
                                    )
                                    Text(
                                        if (isAccessibilityEnabled) "Service is ACTIVE" else "Tap/Swipe requires permission",
                                        fontSize = 12.sp,
                                        color = if (isAccessibilityEnabled) Color(0xFF16A34A) else MayaTextSecondary
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.openAccessibilitySettings() },
                                colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                            ) {
                                Text("Configure", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = MayaNavy)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Notification Listener", fontWeight = FontWeight.Medium, color = MayaTextPrimary)
                                    Text("Read permitted alerts", fontSize = 12.sp, color = MayaTextSecondary)
                                }
                            }
                            Button(
                                onClick = { viewModel.openNotificationSettings() },
                                colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                            ) {
                                Text("Open", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Calls & WhatsApp Communication
            item {
                Text(
                    text = "Communication & Contacts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaNavy
                )

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = contactQuery,
                            onValueChange = {
                                contactQuery = it
                                viewModel.searchContacts(it)
                            },
                            label = { Text("Search contact name or alias...") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = whatsAppText,
                            onValueChange = { whatsAppText = it },
                            label = { Text("WhatsApp message preview...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (matchedContacts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Found ${matchedContacts.size} contacts:",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                            matchedContacts.take(5).forEach { contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = contact.displayName + if (contact.alias != null) " (${contact.alias})" else "",
                                            fontWeight = FontWeight.Medium,
                                            color = MayaTextPrimary
                                        )
                                        Text(
                                            text = contact.phoneNumber,
                                            fontSize = 12.sp,
                                            color = MayaTextSecondary
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = { viewModel.dialContact(contact.phoneNumber) }) {
                                            Icon(Icons.Default.Call, contentDescription = "Dial", tint = Color(0xFF16A34A))
                                        }
                                        IconButton(onClick = { viewModel.sendWhatsApp(contact.phoneNumber, whatsAppText) }) {
                                            Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF0284C7))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick App Launcher
            item {
                Text(
                    text = "Installed Applications (${installedApps.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaNavy
                )

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tap to launch application directly:",
                            fontSize = 13.sp,
                            color = MayaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(installedApps.take(15)) { app ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .clickable { viewModel.launchApp(app.packageName) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = app.appName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MayaNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
