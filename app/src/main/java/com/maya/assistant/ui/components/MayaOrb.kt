package com.maya.assistant.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.assistant.data.model.OrbState
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyanAccent
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaPinkAccent
import com.maya.assistant.ui.theme.MayaPurpleAccent
import com.maya.assistant.ui.theme.MayaShadowDark
import kotlin.math.sin

@Composable
fun MayaOrb(
    orbState: OrbState,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    rmsAmplitude: Float = 0f,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = Color(orbState.primaryColor)
    val secondaryColor = Color(orbState.secondaryColor)

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer Radar & Geometric Tech Rings
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
        ) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val outerRadius = this.size.width / 2 - 8.dp.toPx()
            val midRadius = outerRadius - 16.dp.toPx()

            // Dashed Outer Ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.35f),
                radius = outerRadius,
                center = center,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), 0f)
                )
            )

            // Mid Continuous Ring
            drawCircle(
                color = secondaryColor.copy(alpha = 0.25f),
                radius = midRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 4 Crosshairs (+)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val crosshairDist = this.size.width / 2 - 12.dp.toPx()
            val armLength = 8.dp.toPx()
            val crossColor = primaryColor.copy(alpha = 0.6f)

            // Top
            drawLine(crossColor, Offset(center.x, center.y - crosshairDist - armLength), Offset(center.x, center.y - crosshairDist + armLength), 1.5.dp.toPx())
            drawLine(crossColor, Offset(center.x - armLength, center.y - crosshairDist), Offset(center.x + armLength, center.y - crosshairDist), 1.5.dp.toPx())

            // Bottom
            drawLine(crossColor, Offset(center.x, center.y + crosshairDist - armLength), Offset(center.x, center.y + crosshairDist + armLength), 1.5.dp.toPx())
            drawLine(crossColor, Offset(center.x - armLength, center.y + crosshairDist), Offset(center.x + armLength, center.y + crosshairDist), 1.5.dp.toPx())

            // Left
            drawLine(crossColor, Offset(center.x - crosshairDist, center.y - armLength), Offset(center.x - crosshairDist, center.y + armLength), 1.5.dp.toPx())
            drawLine(crossColor, Offset(center.x - crosshairDist - armLength, center.y), Offset(center.x - crosshairDist + armLength, center.y), 1.5.dp.toPx())

            // Right
            drawLine(crossColor, Offset(center.x + crosshairDist, center.y - armLength), Offset(center.x + crosshairDist, center.y + armLength), 1.5.dp.toPx())
            drawLine(crossColor, Offset(center.x + crosshairDist - armLength, center.y), Offset(center.x + crosshairDist + armLength, center.y), 1.5.dp.toPx())
        }

        // Inner Neumorphic Disc
        val discSize = size * 0.74f
        Box(
            modifier = Modifier
                .size(discSize)
                .scale(if (orbState == OrbState.Speaking || orbState == OrbState.Listening) pulseScale else 1.0f)
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    ambientColor = MayaShadowDark,
                    spotColor = primaryColor.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            MayaBgLight,
                            Color(0xFFD4E2F5)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            primaryColor.copy(alpha = 0.6f),
                            secondaryColor.copy(alpha = 0.4f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top subtitle / category
                Text(
                    text = orbState.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = primaryColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // M.A.Y.A. Title with Glowing Gradient
                Text(
                    text = "M . A . Y . A",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = MayaNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Audio Waveform Visualizer Bars
                AudioWaveVisualizer(
                    orbState = orbState,
                    rmsAmplitude = rmsAmplitude
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Prompt Status Line
                Text(
                    text = orbState.subtitle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun AudioWaveVisualizer(
    orbState: OrbState,
    rmsAmplitude: Float,
    barCount: Int = 18
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val baseColor = Color(orbState.primaryColor)
        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount
            val waveHeight = when (orbState) {
                OrbState.Speaking -> {
                    val sine = sin(phase + normalizedIndex * 4f)
                    (8f + sine * 6f + (normalizedIndex * 4f)).coerceIn(4f, 16f)
                }
                OrbState.Listening -> {
                    val ampBonus = (rmsAmplitude * 1.5f).coerceIn(0f, 12f)
                    val sine = sin(phase * 1.5f + normalizedIndex * 5f)
                    (4f + sine * 3f + ampBonus).coerceIn(3f, 16f)
                }
                OrbState.Processing -> {
                    val sine = sin(phase * 2f + normalizedIndex * 6f)
                    (5f + sine * 4f).coerceIn(3f, 12f)
                }
                else -> {
                    // Gentle subtle idle ripple
                    val sine = sin(phase * 0.5f + normalizedIndex * 3f)
                    (3f + sine * 1.5f).coerceIn(2f, 6f)
                }
            }

            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height(waveHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(baseColor.copy(alpha = 0.75f))
            )
        }
    }
}
