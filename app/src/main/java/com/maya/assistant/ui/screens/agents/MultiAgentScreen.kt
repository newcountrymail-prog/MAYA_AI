package com.maya.assistant.ui.screens.agents

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.maya.assistant.ai.agent.AgentStatus
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun MultiAgentScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MultiAgentViewModel = viewModel()
) {
    val states by viewModel.agentStates.collectAsState()
    val progress by viewModel.pipelineProgress.collectAsState()
    val statusText by viewModel.pipelineStatus.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        TopHeaderBar(
            title = "Sub-Agents",
            isBackButton = true,
            onNavIconClick = onBack,
            onNotificationClick = {}
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pipeline Coordinator Card
            item {
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
                                Icon(Icons.Default.Group, contentDescription = "Agents", tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Autonomous Multi-Agent Swarm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                            }
                            Text(text = "$progress%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF2563EB),
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = statusText, fontSize = 11.sp, color = MayaTextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF2563EB))
                                .clickable {
                                    viewModel.runCooperativeTask(
                                        taskTitle = "Full Stack Feature Build",
                                        prompt = "Write clean architecture code and verify tests"
                                    )
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isExecuting) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Launch", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Launch Cooperative Swarm", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(text = "Specialized Sub-Agents (7)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
            }

            // List of the 7 Agents
            items(states.values.toList()) { agentState ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 3.dp,
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = agentState.agentName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = agentState.currentActivity, fontSize = 11.sp, color = MayaTextSecondary)
                        }

                        val (statusColor, statusBg) = when (agentState.status) {
                            AgentStatus.Completed -> Pair(Color(0xFF16A34A), Color(0xFFDCFCE7))
                            AgentStatus.Executing, AgentStatus.Planning -> Pair(Color(0xFF2563EB), Color(0xFFEFF6FF))
                            AgentStatus.Failed -> Pair(Color(0xFFDC2626), Color(0xFFFEE2E2))
                            else -> Pair(Color(0xFF64748B), Color(0xFFF1F5F9))
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = agentState.status.name.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
