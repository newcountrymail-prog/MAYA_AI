package com.maya.assistant.ui.screens.documents

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.maya.assistant.ai.document.DocumentFormat
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun DocumentsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DocumentsViewModel = viewModel()
) {
    val docName by viewModel.sampleDocumentName.collectAsState()
    val docContent by viewModel.documentContent.collectAsState()
    val summaryResult by viewModel.summaryResult.collectAsState()
    val exportMsg by viewModel.exportMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        TopHeaderBar(
            title = "Documents",
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
            // Document Viewer Card
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
                            Icon(Icons.Default.Description, contentDescription = "Doc", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = docName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "MARKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = docContent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summarize Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2563EB))
                            .clickable { viewModel.summarize() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Summarize, contentDescription = "Summarize", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Summarize Document", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Summary Result Card
            if (summaryResult != null) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Document Synthesis", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        Spacer(modifier = Modifier.height(6.dp))

                        summaryResult!!.keyPoints.forEach { pt ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Text(text = "• ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                Text(text = pt, fontSize = 12.sp, color = MayaTextPrimary, lineHeight = 17.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Export Options", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(DocumentFormat.MARKDOWN, DocumentFormat.TXT, DocumentFormat.PDF).forEach { fmt ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .clickable { viewModel.exportDocument(fmt) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = MayaNavy, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = fmt.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                                    }
                                }
                            }
                        }

                        if (exportMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = exportMsg!!, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
