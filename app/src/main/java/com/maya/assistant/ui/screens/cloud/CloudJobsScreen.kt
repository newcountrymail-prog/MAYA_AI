package com.maya.assistant.ui.screens.cloud

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maya.assistant.cloud.ExecutionTarget
import com.maya.assistant.cloud.JobStatus
import com.maya.assistant.cloud.JobType
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyanAccent
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun CloudJobsScreen(
    onNavigateBack: () -> Unit,
    viewModel: CloudJobsViewModel = viewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    var isCreatingJob by remember { mutableStateOf(false) }
    var newJobTitle by remember { mutableStateOf("") }
    var newJobPrompt by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(JobType.HEAVY_REASONING) }
    var selectedTarget by remember { mutableStateOf(ExecutionTarget.CLOUD_ACCELERATED) }

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cloud Job Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaNavy
                )
                Text(
                    text = "Local-First • Cloud GPU Acceleration • Task Distributor",
                    fontSize = 12.sp,
                    color = MayaTextSecondary
                )
            }
            IconButton(onClick = { isCreatingJob = !isCreatingJob }) {
                Icon(Icons.Default.Add, contentDescription = "Add Job", tint = MayaNavy)
            }
        }

        // Job Creator Card
        if (isCreatingJob) {
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Schedule New Batch Task",
                        fontWeight = FontWeight.Bold,
                        color = MayaNavy,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newJobTitle,
                        onValueChange = { newJobTitle = it },
                        label = { Text("Task Title (e.g. Synthesize Market Data)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newJobPrompt,
                        onValueChange = { newJobPrompt = it },
                        label = { Text("Task Instructions / Payload...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { isCreatingJob = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Cancel", color = MayaTextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newJobTitle.isNotBlank()) {
                                    viewModel.submitJob(
                                        title = newJobTitle,
                                        type = selectedType,
                                        target = selectedTarget,
                                        promptPayload = newJobPrompt
                                    )
                                    newJobTitle = ""
                                    newJobPrompt = ""
                                    isCreatingJob = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MayaNavy)
                        ) {
                            Text("Dispatch", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Architecture Strategy Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudQueue, contentDescription = null, tint = MayaCyanAccent)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Local-First + Cloud Strategy",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MayaNavy
                    )
                    Text(
                        text = "Voice, UI & memory stay local on 3GB RAM device. Heavy reasoning & FLUX art run on cloud GPUs.",
                        fontSize = 11.sp,
                        color = MayaTextSecondary
                    )
                }
            }
        }

        // Jobs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs) { job ->
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (job.target == ExecutionTarget.CLOUD_ACCELERATED)
                                                Color(0xFFE0F2FE)
                                            else Color(0xFFDCFCE7)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (job.target == ExecutionTarget.CLOUD_ACCELERATED)
                                            Icons.Default.Cloud
                                        else Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = if (job.target == ExecutionTarget.CLOUD_ACCELERATED)
                                            MayaNavy
                                        else Color(0xFF16A34A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = job.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MayaTextPrimary
                                    )
                                    Text(
                                        text = "${job.type.name} • ${job.target.name}",
                                        fontSize = 11.sp,
                                        color = MayaTextSecondary
                                    )
                                }
                            }

                            // Status badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (job.status) {
                                            JobStatus.COMPLETED -> Color(0xFFDCFCE7)
                                            JobStatus.RUNNING -> Color(0xFFE0F2FE)
                                            JobStatus.FAILED -> Color(0xFFFEE2E2)
                                            JobStatus.PAUSED -> Color(0xFFFEF3C7)
                                            JobStatus.PENDING -> Color(0xFFF1F5F9)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = job.status.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (job.status) {
                                        JobStatus.COMPLETED -> Color(0xFF16A34A)
                                        JobStatus.RUNNING -> Color(0xFF0284C7)
                                        JobStatus.FAILED -> Color(0xFFEF4444)
                                        JobStatus.PAUSED -> Color(0xFFD97706)
                                        JobStatus.PENDING -> Color(0xFF64748B)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = job.progress / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MayaCyanAccent,
                            trackColor = Color(0xFFE2E8F0)
                        )

                        if (!job.result.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Result: ${job.result}",
                                fontSize = 12.sp,
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Action Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (job.status == JobStatus.RUNNING) {
                                IconButton(onClick = { viewModel.pauseJob(job.id) }) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = MayaNavy)
                                }
                            } else if (job.status == JobStatus.PAUSED) {
                                IconButton(onClick = { viewModel.resumeJob(job.id) }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = MayaNavy)
                                }
                            }

                            if (job.status == JobStatus.FAILED) {
                                IconButton(onClick = { viewModel.retryJob(job.id) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color(0xFF0284C7))
                                }
                            }

                            IconButton(onClick = { viewModel.deleteJob(job.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8))
                            }
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
