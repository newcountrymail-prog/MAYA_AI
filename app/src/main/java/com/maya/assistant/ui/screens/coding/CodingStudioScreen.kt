package com.maya.assistant.ui.screens.coding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
fun CodingStudioScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CodingStudioViewModel = viewModel()
) {
    val prompt by viewModel.codingPrompt.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedCode by viewModel.generatedCode.collectAsState()
    val testRunOutput by viewModel.testRunOutput.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        TopHeaderBar(
            title = "OpenCode Engine",
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
            // Header Info Card
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = "Coding", tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Dedicated Coding Engine", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "READY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.updatePrompt(it) },
                        placeholder = { Text("Describe function, architecture, or algorithm to generate...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Selector
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Kotlin", "Python", "Web", "Rust").forEach { lang ->
                            val isSelected = language == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF2563EB) else Color(0xFFF1F5F9))
                                    .clickable { viewModel.setLanguage(lang) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MayaNavy
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Generate Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF2563EB))
                            .clickable { viewModel.generateCode() }
                            .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Text(text = "Generate Code", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Test Runner Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { viewModel.runTests() }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Run Tests", tint = MayaNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Run Tests", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                            }
                        }
                    }
                }
            }

            // Generated Code Box
            if (generatedCode != null) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = 4.dp,
                    backgroundColor = Color(0xFF0F172A)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = generatedCode!!.fileName,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Verified Clean",
                                fontSize = 10.sp,
                                color = Color(0xFF10B981)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .padding(12.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = generatedCode!!.codeContent,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = generatedCode!!.explanation,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Test Output Terminal
            if (testRunOutput != null) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 3.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Test Execution Output", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = testRunOutput!!,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
