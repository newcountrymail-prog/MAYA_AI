package com.maya.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaShadowDark

@Composable
fun TopHeaderBar(
    title: String,
    modifier: Modifier = Modifier,
    isBackButton: Boolean = false,
    hasNotificationDot: Boolean = true,
    onNavIconClick: () -> Unit,
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Action Button (Hamburger or Back)
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = MayaShadowDark,
                    spotColor = MayaShadowDark
                )
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNavIconClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isBackButton) Icons.Default.ArrowBack else Icons.Default.Menu,
                contentDescription = if (isBackButton) "Back" else "Menu",
                tint = MayaNavy,
                modifier = Modifier.size(22.dp)
            )
        }

        // Center / Main Title
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MayaNavy,
            letterSpacing = (-0.3).sp
        )

        // Right Notification Bell with Red Badge
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = MayaShadowDark,
                    spotColor = MayaShadowDark
                )
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNotificationClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MayaNavy,
                modifier = Modifier.size(22.dp)
            )

            if (hasNotificationDot) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                )
            }
        }
    }
}
