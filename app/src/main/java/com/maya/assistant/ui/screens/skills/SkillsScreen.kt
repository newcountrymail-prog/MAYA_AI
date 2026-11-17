package com.maya.assistant.ui.screens.skills

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.maya.assistant.skills.SkillPlaybook
import com.maya.assistant.skills.StepStatus
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyanAccent
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun SkillsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SkillsViewModel = viewModel()
) {
    val skills by viewModel.availableSkills.collectAsState()
    val activeWorkflow by viewModel.activeWorkflow.collectAsState()
    val logs by viewModel.workflowLogs.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf("All", "Device Control", "Media", "Productivity", "Automation", "Study", "Coding", "Research")

    val filteredSkills = if (selectedCategory == "All") {
        skills
    } else {
        skills.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MayaNavy
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Skills & Automation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaNavy
                )
                Text(
                    text = "Automated Playbooks • Step Execution • Safety Approvals",
                    fontSize = 12.sp,
                    color = MayaTextSecondary
                )
            }
        }

        // Active Workflow Execution Card if active
        if (activeWorkflow != null) {
            val workflow = activeWorkflow!!
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Running: ${workflow.title}",
                                fontWeight = FontWeight.Bold,
                                color = MayaNavy,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${workflow.steps.size} Steps Playbook",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Row {
                            if (isExecuting) {
                                IconButton(onClick = { viewModel.pauseWorkflow() }) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = MayaNavy)
                                }
                            }
                            IconButton(onClick = { viewModel.cancelWorkflow() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color(0xFFEF4444))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Steps List
                    workflow.steps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (step.status) {
                                StepStatus.COMPLETED -> Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(20.dp)
                                )
                                StepStatus.RUNNING -> CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MayaCyanAccent
                                )
                                StepStatus.FAILED -> Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                StepStatus.NEEDS_APPROVAL -> Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(20.dp)
                                )
                                else -> Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MayaTextPrimary
                                )
                                Text(
                                    text = step.description,
                                    fontSize = 11.sp,
                                    color = MayaTextSecondary
                                )
                            }
                            if (step.status == StepStatus.NEEDS_APPROVAL) {
                                Button(
                                    onClick = { viewModel.approveStep(step.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))
                                ) {
                                    Text("Approve", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Recent Log Line
                    if (logs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = logs.last(),
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) MayaNavy else Color(0xFFE2E8F0))
                        .clickable { viewModel.selectCategory(cat) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MayaTextPrimary
                    )
                }
            }
        }

        // Skills List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSkills) { skill ->
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = MayaNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = skill.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = skill.description,
                                fontSize = 12.sp,
                                color = MayaTextSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${skill.category} • ${skill.steps.size} steps",
                                fontSize = 11.sp,
                                color = MayaCyanAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.startWorkflow(skill) },
                            colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run", fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
