package com.maya.assistant.ui.screens.home

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.ui.components.MayaOrb
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyan
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaShadowDark
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToVoice: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val settings by viewModel.userSettings.collectAsState()
    val orbState by viewModel.orbState.collectAsState()
    val rmsAmplitude by viewModel.rmsAmplitude.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val (dayNumber, dayName) = viewModel.getFormattedDate()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Top Header Bar
        TopHeaderBar(
            title = "Maya",
            onNavIconClick = onOpenDrawer,
            onNotificationClick = {}
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Free Mode / License Pill Banner
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0xFFDCE8F8),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "License",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Free mode • aaj ${settings.freeMinutesRemaining}:00 min baaki",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaNavy
                            )
                            Text(
                                text = "Saare tools aur unlimited baat ke liye license activate karo",
                                fontSize = 10.sp,
                                color = MayaTextSecondary
                            )
                        }
                    }
                    Text(
                        text = "Activate karo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Greeting & Energy Balance Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = settings.greetingPrefix,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MayaTextSecondary
                    )
                    Text(
                        text = settings.userName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MayaNavy,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Energy Pill
                NeumorphicCard(
                    shape = RoundedCornerShape(20.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Energy",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${settings.energyBalance}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaNavy
                            )
                            Text(
                                text = "Energy",
                                fontSize = 9.sp,
                                color = MayaTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Centerpiece: Maya Animated Orb
            MayaOrb(
                orbState = orbState,
                size = 250.dp,
                rmsAmplitude = rmsAmplitude,
                onClick = {
                    onNavigateToVoice()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3 Quick Action Pills (Gaane, Padhai, Journal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.MusicNote,
                    label = "Gaane",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onQuickAction("gaane") }
                )
                QuickActionButton(
                    icon = Icons.Default.MenuBook,
                    label = "Padhai",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onQuickAction("padhai") }
                )
                QuickActionButton(
                    icon = Icons.Default.Edit,
                    label = "Journal",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onQuickAction("journal") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Info Widgets (Mausam, Aaj, Mood)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mausam
                InfoWidgetCard(
                    icon = Icons.Default.Cloud,
                    title = "Mausam",
                    mainValue = "—",
                    subtitle = "Data nahi",
                    modifier = Modifier.weight(1f)
                )

                // Aaj (Date)
                InfoWidgetCard(
                    icon = Icons.Default.DateRange,
                    title = "Aaj",
                    mainValue = dayNumber,
                    subtitle = dayName,
                    modifier = Modifier.weight(1f)
                )

                // Mood
                InfoWidgetCard(
                    icon = Icons.Default.Favorite,
                    title = "Mood",
                    mainValue = "Pyaar b...",
                    subtitle = "Sab badhiya",
                    iconTint = Color(0xFFEC4899),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Input Bar
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.95f),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach",
                        tint = MayaTextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onNavigateToChat() }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateInputText(it) },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MayaNavy,
                            fontWeight = FontWeight.Medium
                        ),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Maya se kuchh bhi poochho...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                            .clickable { viewModel.onSendClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MayaNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeumorphicCard(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        elevation = 4.dp,
        backgroundColor = Color.White,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MayaNavy
            )
        }
    }
}

@Composable
private fun InfoWidgetCard(
    icon: ImageVector,
    title: String,
    mainValue: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF0284C7)
) {
    NeumorphicCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MayaTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mainValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MayaNavy
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MayaTextSecondary
            )
        }
    }
}
