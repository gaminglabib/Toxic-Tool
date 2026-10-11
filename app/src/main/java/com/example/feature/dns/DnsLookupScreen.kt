package com.example.feature.dns

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.core.ui.WarningNoticeCard
import com.example.domain.usecase.DnsResult
import com.example.domain.usecase.DnsUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DnsLookupScreen(
    dnsUseCase: DnsUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val scope = rememberCoroutineScope()
    var hostInput by remember { mutableStateOf("cloudflare.com") }
    var isLoading by remember { mutableStateOf(false) }
    var lookupOutcome by remember { mutableStateOf<DnsResult?>(null) }

    val presetDomains = listOf("cloudflare.com", "google.com", "github.com", "android.com")

    fun triggerLookup() {
        if (hostInput.isBlank()) return
        isLoading = true
        lookupOutcome = null
        scope.launch {
            val res = dnsUseCase.resolveHost(hostInput)
            lookupOutcome = res
            isLoading = false
            if (res is DnsResult.Success) {
                onRecordHistory("DNS ${res.hostname}", "${res.records.size} records found")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DNS Lookup", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Resolve IPv4 & IPv6 records asynchronously", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ToxicTextPrimary)
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

            SectionHeader(title = "HOSTNAME / DOMAIN")
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = hostInput,
                onValueChange = { hostInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dns_host_input"),
                placeholder = {
                    Text("e.g., example.com", style = MonospaceCodeStyle, color = ToxicTextMuted)
                },
                trailingIcon = {
                    if (hostInput.isNotEmpty()) {
                        IconButton(onClick = { hostInput = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = ToxicTextMuted)
                        }
                    }
                },
                textStyle = MonospaceCodeStyle,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ToxicBorderFocus,
                    unfocusedBorderColor = ToxicBorder,
                    focusedContainerColor = ToxicCodeBg,
                    unfocusedContainerColor = ToxicCodeBg,
                    focusedTextColor = ToxicTextPrimary,
                    unfocusedTextColor = ToxicTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presetDomains.forEach { domain ->
                    FilterChip(
                        selected = hostInput == domain,
                        onClick = {
                            hostInput = domain
                            triggerLookup()
                        },
                        label = { Text(domain, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToxicEmeraldDark,
                            selectedLabelColor = ToxicEmerald,
                            containerColor = ToxicSurface,
                            labelColor = ToxicTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CyberButton(
                text = if (isLoading) "Resolving..." else "Resolve DNS Records",
                onClick = { triggerLookup() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Search,
                enabled = !isLoading && hostInput.isNotBlank(),
                testTag = "resolve_dns_btn"
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ToxicEmerald)
                }
            }

            when (val res = lookupOutcome) {
                is DnsResult.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    WarningNoticeCard(
                        title = "RESOLUTION FAILED",
                        message = res.message
                    )
                }
                is DnsResult.Success -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberBadge(text = "${res.records.size} IP(s)", color = ToxicEmeraldBright, backgroundColor = ToxicEmeraldDark)
                        CyberBadge(text = "${res.lookupDurationMs} ms", color = ToxicCyan, backgroundColor = ToxicSurface)
                        CyberBadge(text = res.hostname, color = ToxicTextSecondary, backgroundColor = ToxicSurface)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "RESOLVED ADDRESSES",
                                style = MaterialTheme.typography.labelSmall,
                                color = ToxicEmerald
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            res.records.forEach { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = record.ip, style = MonospaceCodeStyle, color = ToxicTextPrimary)
                                        if (record.canonicalName.isNotBlank() && record.canonicalName != record.ip) {
                                            Text(text = "CNAME: ${record.canonicalName}", style = MaterialTheme.typography.bodySmall, color = ToxicTextMuted)
                                        }
                                    }
                                    CyberBadge(text = record.type, color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                                }
                            }
                        }
                    }
                }
                null -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
