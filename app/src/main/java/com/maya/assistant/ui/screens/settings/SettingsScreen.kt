package com.maya.assistant.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Zap
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.userSettings.collectAsState()
    var showSecretDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Top Header Bar
        TopHeaderBar(
            title = "Settings",
            isBackButton = true,
            onNavIconClick = onBack,
            onNotificationClick = {}
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: ACCOUNT
            SettingsSectionHeader("ACCOUNT")
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                SettingsItemRow(
                    icon = Icons.Default.Person,
                    title = "Personal",
                    subtitle = "Tumhara naam, music, Gemini aur YouTube keys",
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF3B82F6),
                    onClick = { }
                )
            }

            // Section 2: ASSISTANT
            SettingsSectionHeader("ASSISTANT")
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Default.Headphones,
                        title = "Maya",
                        subtitle = "Persona, awaaz, bhasha (${settings.voiceLanguage})",
                        iconBg = Color(0xFFF0FDF4),
                        iconTint = Color(0xFF10B981),
                        onClick = {
                            val newLang = if (settings.voiceLanguage == "hi-IN") "en-IN" else "hi-IN"
                            viewModel.updateVoiceLanguage(newLang)
                        }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.RecordVoiceOver,
                        title = "Voice engine",
                        subtitle = "Normal ya Advanced Agentic — wo kaise sunti, sochti aur bolti hai",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Lock,
                        title = "Secret settings",
                        subtitle = if (settings.isGirlfriendMode) "Girlfriend mode Active (Romantic)" else "Girlfriend mode, Romantic, Intense (18+)",
                        iconBg = Color(0xFFFFF1F2),
                        iconTint = Color(0xFFE11D48),
                        onClick = { viewModel.toggleGirlfriendMode() }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Tune,
                        title = "Skills",
                        subtitle = "Installed playbooks aur online skill store",
                        iconBg = Color(0xFFFEF9C3),
                        iconTint = Color(0xFFCA8A04),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.SmartToy,
                        title = "Sub-agents",
                        subtitle = "Coding models aur background agents",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF0284C7),
                        onClick = { }
                    )
                }
            }

            // Section 3: KAAM AUR MESSAGES
            SettingsSectionHeader("KAAM AUR MESSAGES")
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Default.Email,
                        title = "Email",
                        subtitle = "Maya tumhare address se mail bhej sake",
                        iconBg = Color(0xFFF0FDF4),
                        iconTint = Color(0xFF059669),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Chat,
                        title = "Messages aur WhatsApp",
                        subtitle = "Direct voice prompt se auto chat bhejein",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = { }
                    )
                }
            }

            // Link to Advanced Settings
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = 3.dp,
                backgroundColor = Color(0xFFE0F2FE),
                onClick = onNavigateToAdvanced
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Advanced Settings Kholen",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Advanced",
                        tint = Color(0xFF0369A1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = MayaTextSecondary,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    )
}

@Composable
fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color = Color(0xFFEFF6FF),
    iconTint: Color = Color(0xFF3B82F6),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MayaTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Open",
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(14.dp)
        )
    }
}
