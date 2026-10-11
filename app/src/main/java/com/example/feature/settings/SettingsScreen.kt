package com.example.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.SectionHeader
import com.example.data.repository.ToolboxRepository
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: ToolboxRepository,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isHistoryEnabled by repository.preferences.isHistoryEnabled.collectAsState(initial = true)
    val indentSpaces by repository.preferences.indentSpaces.collectAsState(initial = 2)

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Settings", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Preferences & privacy controls", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ToxicBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ToxicBackground,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // General Preferences
            SectionHeader(title = "USER PREFERENCES", subtitle = "Persisted locally on device")
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ToxicBorder,
                backgroundColor = ToxicSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Record Activity History", style = MaterialTheme.typography.bodyLarge, color = ToxicTextPrimary)
                            Text("Logs non-sensitive operations to local SQLite", style = MaterialTheme.typography.bodySmall, color = ToxicTextMuted)
                        }
                        Switch(
                            checked = isHistoryEnabled,
                            onCheckedChange = {
                                scope.launch { repository.preferences.setHistoryEnabled(it) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ToxicEmerald,
                                checkedTrackColor = ToxicEmeraldDark
                            ),
                            modifier = Modifier.testTag("toggle_history_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Default JSON Indentation", style = MaterialTheme.typography.bodyLarge, color = ToxicTextPrimary)
                            Text("Spaces used during format & beautify", style = MaterialTheme.typography.bodySmall, color = ToxicTextMuted)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(2, 4).forEach { sp ->
                                FilterChip(
                                    selected = indentSpaces == sp,
                                    onClick = {
                                        scope.launch { repository.preferences.setIndentSpaces(sp) }
                                    },
                                    label = { Text("$sp spaces", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ToxicEmeraldDark,
                                        selectedLabelColor = ToxicEmerald,
                                        containerColor = ToxicBackground,
                                        labelColor = ToxicTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // System & Device Architecture Info
            SectionHeader(title = "DEVICE OPTIMIZATION", subtitle = "Hardware and runtime metrics")
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ToxicBorder,
                backgroundColor = ToxicSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Target Architecture", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        CyberBadge(text = "Android 9+ (API 28)", color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Device Optimization Target", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("Vivo Y19 (Helio P65 / 4GB RAM)", style = MaterialTheme.typography.bodySmall, color = ToxicTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Data Storage", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("Offline Room (SQLite) + DataStore", style = MaterialTheme.typography.bodySmall, color = ToxicCyan)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cloud Dependencies", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        CyberBadge(text = "NONE (ZERO CLOUD)", color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // About & Privacy navigation
            SectionHeader(title = "ABOUT & PRIVACY")
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToAbout,
                borderColor = ToxicBorder,
                backgroundColor = ToxicSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = ToxicEmerald)
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        Column {
                            Text("About TOXIC TOOLBOX", style = MaterialTheme.typography.bodyLarge, color = ToxicTextPrimary)
                            Text("Manifesto, license & zero-tracking policy", style = MaterialTheme.typography.bodySmall, color = ToxicTextMuted)
                        }
                    }
                    Text("→", color = ToxicEmerald, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Danger Zone / Data Reset
            SectionHeader(title = "DANGER ZONE", subtitle = "Destructive data operations")
            Spacer(modifier = Modifier.height(10.dp))

            CyberButton(
                text = "Reset All User Data & History",
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                isDestructive = true,
                isOutlined = true,
                icon = Icons.Filled.Warning,
                testTag = "reset_all_data_btn"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Erase All Data?", color = ToxicAccentRed) },
                text = {
                    Text(
                        "This will permanently erase all activity history, favorite shortcuts, and custom preferences stored on this device.",
                        color = ToxicTextSecondary
                    )
                },
                confirmButton = {
                    CyberButton(
                        text = "Reset Everything",
                        isDestructive = true,
                        onClick = {
                            scope.launch {
                                repository.resetAllUserData()
                                showResetDialog = false
                                snackbarHostState.showSnackbar("All local data has been reset.")
                            }
                        },
                        testTag = "confirm_reset_btn"
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = ToxicTextSecondary)
                    }
                },
                containerColor = ToxicSurface
            )
        }
    }
}
