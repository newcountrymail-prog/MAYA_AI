package com.maya.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyanAccent
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaShadowDark
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun MayaDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(MayaBgLight)
            .padding(vertical = 24.dp, horizontal = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(8.dp, CircleShape, ambientColor = MayaCyanAccent)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Maya Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Maya",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaNavy
                )
                Text(
                    text = "MAYA AI Assistant",
                    fontSize = 12.sp,
                    color = MayaTextSecondary
                )
            }
        }

        // Section HOME
        DrawerSectionTitle("HOME")
        DrawerItem(
            icon = Icons.Default.Home,
            title = "Home",
            isSelected = currentRoute == "home",
            onClick = { onNavigate("home") }
        )
        DrawerItem(
            icon = Icons.Default.GraphicEq,
            title = "Maya Home",
            isSelected = currentRoute == "voice",
            onClick = { onNavigate("voice") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section INTELLIGENCE & CREATIVE
        DrawerSectionTitle("AI & CREATIVE")
        DrawerItem(
            icon = Icons.Default.Hub,
            title = "AI Providers",
            isSelected = currentRoute == "ai_providers",
            onClick = { onNavigate("ai_providers") }
        )
        DrawerItem(
            icon = Icons.Default.Code,
            title = "Website / Coding",
            isSelected = currentRoute == "coding_studio",
            onClick = { onNavigate("coding_studio") }
        )
        DrawerItem(
            icon = Icons.Default.Image,
            title = "Image Studio",
            isSelected = currentRoute == "image_studio",
            onClick = { onNavigate("image_studio") }
        )
        DrawerItem(
            icon = Icons.Default.Search,
            title = "Research",
            isSelected = currentRoute == "research",
            onClick = { onNavigate("research") }
        )
        DrawerItem(
            icon = Icons.Default.Article,
            title = "Documents",
            isSelected = currentRoute == "documents",
            onClick = { onNavigate("documents") }
        )
        DrawerItem(
            icon = Icons.Default.Group,
            title = "Sub-agents",
            isSelected = currentRoute == "multi_agent",
            onClick = { onNavigate("multi_agent") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section KAAM-KAAJ
        DrawerSectionTitle("KAAM-KAAJ")
        DrawerItem(
            icon = Icons.Default.TrendingUp,
            title = "Markets",
            isSelected = false,
            onClick = { onNavigate("home") }
        )
        DrawerItem(
            icon = Icons.Default.Storefront,
            title = "Marketing",
            isSelected = false,
            onClick = { onNavigate("home") }
        )
        DrawerItem(
            icon = Icons.Default.PhotoLibrary,
            title = "Meri photos",
            isSelected = false,
            onClick = { onNavigate("scanner") }
        )
        DrawerItem(
            icon = Icons.Default.Language,
            title = "Browser",
            isSelected = false,
            onClick = { onNavigate("home") }
        )
        DrawerItem(
            icon = Icons.Default.AccessibilityNew,
            title = "Accessibility",
            isSelected = false,
            onClick = { onNavigate("settings") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section SETTINGS
        DrawerSectionTitle("SETTINGS & SYSTEM")
        DrawerItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            isSelected = currentRoute == "settings",
            onClick = { onNavigate("settings") }
        )

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "v1.0.0 • MAYA AI",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MayaTextSecondary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MayaTextSecondary,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = MayaShadowDark
            )
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFE0F2FE) else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF0284C7) else MayaNavy,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color(0xFF0284C7) else MayaTextPrimary
            )
        }
    }
}
