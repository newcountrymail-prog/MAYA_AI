package com.maya.assistant.ui.screens.providers

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.maya.assistant.data.model.AiProvider
import com.maya.assistant.data.model.ConnectionStatus
import com.maya.assistant.data.model.PricingTier
import com.maya.assistant.ui.components.NeumorphicCard
import com.maya.assistant.ui.components.TopHeaderBar
import com.maya.assistant.ui.theme.MayaBgLight
import com.maya.assistant.ui.theme.MayaCyan
import com.maya.assistant.ui.theme.MayaNavy
import com.maya.assistant.ui.theme.MayaTextPrimary
import com.maya.assistant.ui.theme.MayaTextSecondary

@Composable
fun ProvidersScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProvidersViewModel = viewModel()
) {
    val providers by viewModel.providers.collectAsState()
    val testingId by viewModel.testingProviderId.collectAsState()
    val connectionResult by viewModel.connectionResult.collectAsState()
    val discoveredModels by viewModel.discoveredModels.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showModelsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaBgLight)
    ) {
        TopHeaderBar(
            title = "AI Providers",
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Universal AI Hub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaNavy
                        )
                        Text(
                            text = "${providers.size} configured • Free-mode protected",
                            fontSize = 12.sp,
                            color = MayaTextSecondary
                        )
                    }

                    // Add Provider Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF2563EB))
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            items(providers) { provider ->
                val isTesting = testingId == provider.id
                ProviderCard(
                    provider = provider,
                    isTesting = isTesting,
                    onToggle = { viewModel.toggleProvider(provider) },
                    onSetDefault = { viewModel.setDefaultProvider(provider.id) },
                    onTestConnection = { viewModel.testConnection(provider) },
                    onDiscoverModels = {
                        viewModel.discoverModels(provider)
                        showModelsDialog = true
                    },
                    onDelete = { viewModel.deleteProvider(provider.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Add Provider Dialog
    if (showAddDialog) {
        AddProviderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, key, url, model ->
                viewModel.addCustomProvider(name, key, url, model)
                showAddDialog = false
            }
        )
    }

    // Discovered Models Dialog
    if (showModelsDialog) {
        AlertDialog(
            onDismissRequest = { showModelsDialog = false },
            title = { Text(text = "Discovered Models", fontWeight = FontWeight.Bold, color = MayaNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (discoveredModels.isEmpty()) {
                        Text("No verified models returned.", fontSize = 13.sp, color = MayaTextSecondary)
                    } else {
                        discoveredModels.forEach { model ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = model.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                                    Text(text = "Context: ${model.contextLength} tokens", fontSize = 11.sp, color = MayaTextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (model.pricingTier == PricingTier.VerifiedFree || model.pricingTier == PricingTier.FreeTier) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = model.pricingTier.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (model.pricingTier == PricingTier.VerifiedFree || model.pricingTier == PricingTier.FreeTier) Color(0xFF16A34A) else MayaNavy
                                    )
                                }
                            }
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModelsDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ProviderCard(
    provider: AiProvider,
    isTesting: Boolean,
    onToggle: () -> Unit,
    onSetDefault: () -> Unit,
    onTestConnection: () -> Unit,
    onDiscoverModels: () -> Unit,
    onDelete: () -> Unit
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Name & Pricing Badge
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
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Hub, contentDescription = "Provider", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = provider.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                            if (provider.isDefault) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Star, contentDescription = "Default", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = "Model: ${provider.selectedModel}", fontSize = 12.sp, color = MayaTextSecondary)
                    }
                }

                // Pricing Badge
                val isFree = provider.isFree
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFree) Color(0xFFDCFCE7) else Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = provider.pricingTier.displayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFree) Color(0xFF16A34A) else Color(0xFF2563EB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Key & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Key: ${provider.maskedApiKey}",
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF64748B)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (provider.connectionStatus) {
                        ConnectionStatus.Connected -> Color(0xFF10B981)
                        ConnectionStatus.Failed -> Color(0xFFEF4444)
                        ConnectionStatus.Testing -> Color(0xFFF59E0B)
                        else -> Color(0xFF94A3B8)
                    }
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(statusColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = provider.connectionStatus.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Test Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable(onClick = onTestConnection)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = MayaNavy)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = MayaNavy, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Test", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                            }
                        }
                    }

                    // Discover Models Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable(onClick = onDiscoverModels)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Explore, contentDescription = "Models", tint = MayaNavy, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "Models", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MayaNavy)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = provider.isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2563EB))
                    )
                }
            }
        }
    }
}

@Composable
private fun AddProviderDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, key: String, url: String, model: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("https://api.openai.com/v1") }
    var model by remember { mutableStateOf("gpt-4o") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add AI Provider", fontWeight = FontWeight.Bold, color = MayaNavy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Provider Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, key, url, model) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Save Provider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
