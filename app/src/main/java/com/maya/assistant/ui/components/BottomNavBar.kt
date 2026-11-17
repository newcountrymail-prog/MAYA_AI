package com.maya.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.assistant.ui.theme.MayaCyan
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaShadowDark
import com.maya.assistant.ui.theme.MayaTextMuted

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Dock Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(34.dp),
                    ambientColor = MayaShadowDark,
                    spotColor = MayaShadowDark
                )
                .clip(RoundedCornerShape(34.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                NavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentRoute == "home",
                    onClick = { onNavigate("home") }
                )

                // 2. Scan
                NavItem(
                    icon = Icons.Default.CropFree,
                    label = "Scan",
                    isSelected = currentRoute == "scanner",
                    onClick = { onNavigate("scanner") }
                )

                // Spacer for elevated Center Mic button
                Box(modifier = Modifier.size(56.dp))

                // 4. Yaadein
                NavItem(
                    icon = Icons.Default.Psychology,
                    label = "Yaadein",
                    isSelected = currentRoute == "memories",
                    onClick = { onNavigate("memories") }
                )

                // 5. Chat
                NavItem(
                    icon = Icons.Default.Chat,
                    label = "Chat",
                    isSelected = currentRoute == "chat",
                    onClick = { onNavigate("chat") }
                )
            }
        }

        // Center Elevated Glowing Mic Button
        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .size(68.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = MayaCyan,
                    spotColor = MayaCyan
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF60A5FA),
                            Color(0xFF3B82F6),
                            Color(0xFF1D4ED8)
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onMicClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "MAYA Voice",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeBg = if (isSelected) Color(0xFFE0F2FE) else Color.Transparent
    val contentColor = if (isSelected) MayaNavy else MayaTextMuted

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(activeBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
