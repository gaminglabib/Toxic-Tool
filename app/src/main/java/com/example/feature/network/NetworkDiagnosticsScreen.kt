package com.example.feature.network

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.PrivacyNoticeBanner
import com.example.core.ui.SectionHeader
import com.example.domain.usecase.NetworkDiagnosticState
import com.example.domain.usecase.NetworkDiagnosticsUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicRedDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicSurfaceVariant
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDiagnosticsScreen(
    networkUseCase: NetworkDiagnosticsUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(networkUseCase.checkInterfaceStatus()) }
    var isProbing by remember { mutableStateOf(false) }

    fun refreshTelemetry() {
        state = networkUseCase.checkInterfaceStatus()
    }

    fun runHttpsProbe() {
        isProbing = true
        scope.launch {
            val (reachable, latency) = networkUseCase.performHttpsProbe()
            val base = networkUseCase.checkInterfaceStatus()
            state = base.copy(
                probeStatus = if (reachable) "Reachable ($latency ms)" else "Probe Timeout / Unreachable",
                probeLatencyMs = latency
            )
            isProbing = false
            onRecordHistory("Network Diagnostic", "Interface: ${state.transportType}, Validated: ${state.hasValidatedInternet}")
        }
    }

    LaunchedEffect(Unit) {
        refreshTelemetry()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Network Diagnostics", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Interface status & validated reachability", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ToxicTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { refreshTelemetry() }, modifier = Modifier.testTag("refresh_net_btn")) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = ToxicEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ToxicBackground)
            )
        },
        containerColor = ToxicBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PrivacyNoticeBanner(
                message = "Diagnostics query local device network interfaces only. Public IP addresses are not stored or tracked."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Interface Overview Card
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
                        Text(
                            text = "INTERFACE STATUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ToxicEmerald
                        )
                        Text(
                            text = "Timestamp: ${state.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ToxicTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Network Connected", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        CyberBadge(
                            text = if (state.isConnected) "CONNECTED" else "DISCONNECTED",
                            color = if (state.isConnected) ToxicEmeraldBright else ToxicAccentRed,
                            backgroundColor = if (state.isConnected) ToxicEmeraldDark else ToxicRedDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Internet Validated", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        CyberBadge(
                            text = if (state.hasValidatedInternet) "CONFIRMED" else "NOT VALIDATED",
                            color = if (state.hasValidatedInternet) ToxicEmeraldBright else ToxicCyan,
                            backgroundColor = if (state.hasValidatedInternet) ToxicEmeraldDark else ToxicSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Transport Route", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text(state.transportType, style = MonospaceCodeStyle, color = ToxicTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Metered Connection", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text(if (state.isMetered) "Yes (Metered)" else "No (Unmetered)", style = MonospaceCodeStyle, color = ToxicTextSecondary)
                    }

                    if (state.linkDownstreamBandwidthKbps != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Downstream Link", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                            Text("${state.linkDownstreamBandwidthKbps} Kbps", style = MonospaceCodeStyle, color = ToxicCyan)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HTTPS Reachability Probe Card
            SectionHeader(title = "ACTIVE CONNECTIVITY PROBE", subtitle = "Test confirmed external HTTPS handshakes")
            Spacer(modifier = Modifier.height(6.dp))

            CyberButton(
                text = if (isProbing) "Probing HTTPS Gateway..." else "Test HTTPS Reachability (1.1.1.1)",
                onClick = { runHttpsProbe() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Sensors,
                enabled = !isProbing && state.isConnected,
                testTag = "https_probe_btn"
            )

            if (isProbing) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ToxicEmerald)
                }
            }

            if (state.probeStatus != null) {
                Spacer(modifier = Modifier.height(14.dp))
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (state.probeLatencyMs != null) ToxicEmeraldDark else ToxicAccentRed,
                    backgroundColor = ToxicSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("External HTTPS Handshake:", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        CyberBadge(
                            text = state.probeStatus!!,
                            color = if (state.probeLatencyMs != null) ToxicEmeraldBright else ToxicAccentRed,
                            backgroundColor = if (state.probeLatencyMs != null) ToxicEmeraldDark else ToxicRedDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
