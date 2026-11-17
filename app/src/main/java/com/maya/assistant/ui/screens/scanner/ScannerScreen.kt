package com.maya.assistant.ui.screens.scanner

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaHudBg
import com.maya.assistant.ui.theme.MayaHudCyan

@Composable
fun ScannerScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel = viewModel()
) {
    val isBackCamera by viewModel.isBackCamera.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val feedState by viewModel.feedState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "hud_rotation")
    val hudRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Top Header Bar
        TopHeaderBar(
            title = "Scanner",
            onNavIconClick = onOpenDrawer,
            onNotificationClick = {}
        )

        // Viewport Area (Dark Cyberpunk HUD)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MayaHudBg)
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
        ) {
            // Subtle HUD Grid Lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 40.dp.toPx()
                val gridColor = Color(0xFF0F1E33)
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
                    y += step
                }
            }

            // Top Status Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MayaHudCyan)
                    )
                    Text(
                        text = " MAYA VISION",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MayaHudCyan,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = if (isBackCamera) "BACK CAM" else "FRONT CAM",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF64748B)
                )
            }

            // Standby Prompt Terminal Box
            Box(
                modifier = Modifier
                    .padding(top = 56.dp, start = 20.dp, end = 20.dp)
                    .border(1.dp, Color(0xFF1E3A8A).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A1322).copy(alpha = 0.8f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF93C5FD),
                    lineHeight = 16.sp
                )
            }

            // Center Radar / HUD Reticle
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(hudRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 10f

                    // Dashed Outer Ring
                    drawCircle(
                        color = Color(0xFF1E3A8A),
                        radius = radius,
                        center = center,
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                    )

                    // Rotating Cyan Arcs
                    drawArc(
                        color = MayaHudCyan,
                        startAngle = 45f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius * 0.75f, center.y - radius * 0.75f),
                        size = Size(radius * 1.5f, radius * 1.5f),
                        style = Stroke(width = 3.5f)
                    )

                    drawArc(
                        color = MayaHudCyan,
                        startAngle = 225f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius * 0.75f, center.y - radius * 0.75f),
                        size = Size(radius * 1.5f, radius * 1.5f),
                        style = Stroke(width = 3.5f)
                    )
                }

                // Crosshair Reticle Center
                Canvas(modifier = Modifier.size(80.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    drawLine(Color(0xFF38BDF8), Offset(center.x, 0f), Offset(center.x, 24f), 2f)
                    drawLine(Color(0xFF38BDF8), Offset(center.x, size.height - 24f), Offset(center.x, size.height), 2f)
                    drawLine(Color(0xFF38BDF8), Offset(0f, center.y), Offset(24f, center.y), 2f)
                    drawLine(Color(0xFF38BDF8), Offset(size.width - 24f, center.y), Offset(size.width, center.y), 2f)
                    drawCircle(MayaHudCyan, radius = 5f, center = center)
                }
            }

            // Bottom Telemetry Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 100.dp)
            ) {
                Text(
                    text = "FRM 004184   SIG 68",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = feedState,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (feedState.contains("ACTIVE") || feedState.contains("STREAMING")) MayaHudCyan else Color(0xFF64748B)
                )
            }

            // Controls Bar (Flip, Shutter Button, Rec)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flip Camera
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.flipCamera() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1E33)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = "Flip Camera",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "FLIP",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Shutter Capture Button with Outer White Bloom
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(16.dp, CircleShape, ambientColor = MayaHudCyan, spotColor = MayaHudCyan)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8).copy(alpha = 0.3f))
                        .clickable { viewModel.capturePhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Rec Video Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.toggleRecording() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRecording) Color(0xFFDC2626) else Color(0xFF0F1E33)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Record",
                            tint = if (isRecording) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRecording) "STOP" else "REC",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) Color(0xFFEF4444) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
