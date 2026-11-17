package com.maya.assistant.ui.screens.voice

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.ui.components.MayaOrb
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyan
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaShadowDark
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun VoiceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoiceViewModel = viewModel()
) {
    val orbState by viewModel.orbState.collectAsState()
    val rmsAmplitude by viewModel.rmsAmplitude.collectAsState()
    val transcription by viewModel.transcription.collectAsState()
    val lastResponse by viewModel.lastResponse.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    LaunchedEffect(Unit) {
        if (orbState == OrbState.Idle) {
            viewModel.startListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape, ambientColor = MayaShadowDark)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MayaNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "MAYA Voice",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MayaNavy
            )

            // Language Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFE0F2FE))
                    .clickable { viewModel.toggleVoiceLanguage() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = MayaCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (userSettings.voiceLanguage == "hi-IN") "हिंदी" else "English",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Animated Orb
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            MayaOrb(
                orbState = orbState,
                size = 280.dp,
                rmsAmplitude = rmsAmplitude,
                onClick = {
                    if (orbState == OrbState.Speaking) {
                        viewModel.interrupt()
                    } else if (orbState == OrbState.Idle) {
                        viewModel.startListening()
                    }
                }
            )
        }

        // Live Transcription / Response Container
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = 6.dp,
            backgroundColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (orbState) {
                        OrbState.Listening -> if (transcription.isBlank()) "Boliye, main sun rahi hoon..." else "\"$transcription\""
                        OrbState.Processing -> "Soch rahi hoon..."
                        OrbState.Speaking -> lastResponse.ifBlank { "Maya bol rahi hai..." }
                        else -> "Tap karke baat shuru karein"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MayaTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Voice Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop / Interrupt Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(8.dp, CircleShape, ambientColor = MayaShadowDark)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { viewModel.interrupt() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // Main Microphone Toggle Button
            val isListening = orbState == OrbState.Listening
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(14.dp, CircleShape, ambientColor = MayaCyan, spotColor = MayaCyan)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                        )
                    )
                    .clickable {
                        if (isListening) viewModel.stopListening() else viewModel.startListening()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
