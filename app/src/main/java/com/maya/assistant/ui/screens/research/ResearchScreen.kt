package com.maya.assistant.ui.screens.research

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
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
fun ResearchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResearchViewModel = viewModel()
) {
    val query by viewModel.query.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val report by viewModel.report.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        TopHeaderBar(
            title = "Research",
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
            // Search Input Card
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Deep Web Research", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.updateQuery(it) },
                        placeholder = { Text("Topic, market trend, or technical question...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2563EB))
                            .clickable { viewModel.performResearch() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Analyze & Generate Report", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Results & Citations
            if (report != null) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Synthesized Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = report!!.structuredReport,
                            fontSize = 13.sp,
                            color = MayaTextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Verified Sources & Citations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        Spacer(modifier = Modifier.height(8.dp))

                        report!!.citations.forEach { citation ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Source", tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = citation.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                    Text(text = citation.snippet, fontSize = 11.sp, color = MayaTextSecondary, lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
