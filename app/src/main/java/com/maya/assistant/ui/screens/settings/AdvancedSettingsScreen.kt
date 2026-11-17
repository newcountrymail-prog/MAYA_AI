package com.maya.assistant.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight

@Composable
fun AdvancedSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Top Header Bar
        TopHeaderBar(
            title = "Advanced",
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
            // Section 1: LOOK AUR FEEL
            SettingsSectionHeader("LOOK AUR FEEL")
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Theme",
                        subtitle = "Colours, font, text size aur corners",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = { viewModel.toggleTheme() }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Favorite,
                        title = "Appearance",
                        subtitle = "Orb style, colour aur size",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF3B82F6),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Behaviour",
                        subtitle = "Floating orb, echo guard, boot pe start",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF0284C7),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Edit,
                        title = "Typing",
                        subtitle = "Editors me insaan jaisi speed se typing",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF1D4ED8),
                        onClick = { }
                    )
                }
            }

            // Section 2: SAFETY AUR ACCESS
            SettingsSectionHeader("SAFETY AUR ACCESS")
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Default.Security,
                        title = "Voice Guardian",
                        subtitle = "Sirf tumhari awaaz ka jawab de",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Face,
                        title = "Chehre",
                        subtitle = "Tumhe aur tumhare logon ko pehchane, sirf isi phone pe",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF38BDF8),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Emergency SOS",
                        subtitle = "Contacts, siren aur kya bheja jaata hai",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF0284C7),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.TouchApp,
                        title = "Touch Guard",
                        subtitle = "Tum door ho tab phone pe nazar rakhe",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF0284C7),
                        onClick = { }
                    )
                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsItemRow(
                        icon = Icons.Default.Lock,
                        title = "Screen lock",
                        subtitle = "Phone jagana, aur tumhare liye unlock karna",
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF0284C7),
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
