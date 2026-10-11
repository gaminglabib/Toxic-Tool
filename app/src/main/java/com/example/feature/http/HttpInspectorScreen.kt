package com.example.feature.http

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
import androidx.compose.material.icons.filled.Send
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
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.core.ui.WarningNoticeCard
import com.example.domain.usecase.HttpInspectionOutcome
import com.example.domain.usecase.HttpInspectorUseCase
import com.example.domain.usecase.HttpResponseResult
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicRedDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HttpInspectorScreen(
    httpInspectorUseCase: HttpInspectorUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val scope = rememberCoroutineScope()

    var urlInput by remember { mutableStateOf("https://httpbin.org/get") }
    var method by remember { mutableStateOf("GET") }
    var isLoading by remember { mutableStateOf(false) }
    var inspectionResult by remember { mutableStateOf<HttpInspectionOutcome?>(null) }

    val isInsecureHttp = remember(urlInput) {
        httpInspectorUseCase.isPlainHttp(urlInput)
    }

    fun executeTest() {
        if (urlInput.isBlank()) return
        isLoading = true
        inspectionResult = null
        scope.launch {
            val outcome = httpInspectorUseCase.executeRequest(urlInput, method)
            inspectionResult = outcome
            isLoading = false
            if (outcome is HttpInspectionOutcome.Success) {
                onRecordHistory("HTTP $method ${outcome.response.statusCode}", urlInput)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("HTTP Inspector", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Headers, status & latency inspection", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ToxicTextPrimary)
                    }
                },
                actions = {
                    CyberButton(
                        text = "Sample",
                        onClick = { urlInput = "https://httpbin.org/get" },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "http_sample_btn"
                    )
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

            // Method Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("GET", "HEAD").forEach { m ->
                    FilterChip(
                        selected = method == m,
                        onClick = { method = m },
                        label = { Text(m, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToxicEmeraldDark,
                            selectedLabelColor = ToxicEmerald,
                            containerColor = ToxicSurface,
                            labelColor = ToxicTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SectionHeader(title = "TARGET URL")
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("http_url_field"),
                placeholder = {
                    Text("https://api.example.com/v1/health", style = MonospaceCodeStyle, color = ToxicTextMuted)
                },
                trailingIcon = {
                    if (urlInput.isNotEmpty()) {
                        IconButton(onClick = { urlInput = "" }) {
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

            if (isInsecureHttp) {
                Spacer(modifier = Modifier.height(8.dp))
                WarningNoticeCard(
                    title = "INSECURE CLEAR-TEXT HTTP",
                    message = "Plain HTTP transmits data unencrypted. Use HTTPS to protect against traffic sniffing and tampering."
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = if (isLoading) "Sending Request..." else "Send $method Request",
                onClick = { executeTest() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Send,
                enabled = !isLoading && urlInput.isNotBlank(),
                testTag = "send_http_btn"
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = ToxicEmerald)
                }
            }

            when (val res = inspectionResult) {
                is HttpInspectionOutcome.SecurityBlocked -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    WarningNoticeCard(
                        title = "SSRF SECURITY GUARDRAIL TRIGGERED",
                        message = res.reason
                    )
                }
                is HttpInspectionOutcome.NetworkError -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    WarningNoticeCard(
                        title = "${res.errorType.uppercase()} FAILURE",
                        message = res.message
                    )
                }
                is HttpInspectionOutcome.Success -> {
                    val r = res.response
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status and Latency Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusColor = when (r.statusCode) {
                            in 200..299 -> ToxicEmeraldBright
                            in 300..399 -> ToxicCyan
                            else -> ToxicAccentRed
                        }
                        val statusBg = when (r.statusCode) {
                            in 200..299 -> ToxicEmeraldDark
                            in 300..399 -> ToxicCyan.copy(alpha = 0.2f)
                            else -> ToxicRedDark
                        }
                        CyberBadge(text = "${r.statusCode} ${r.statusMessage}", color = statusColor, backgroundColor = statusBg)
                        CyberBadge(text = "${r.durationMs} ms", color = ToxicEmeraldBright, backgroundColor = ToxicSurface)
                        CyberBadge(text = r.protocol, color = ToxicTextSecondary, backgroundColor = ToxicSurface)
                        if (r.isHttps) {
                            CyberBadge(text = "TLS/HTTPS", color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Response Headers Card
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "RESPONSE HEADERS (${r.headers.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = ToxicEmerald
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            r.headers.take(15).forEach { (k, v) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(text = "$k: ", style = MonospaceCodeStyle, color = ToxicCyan)
                                    Text(text = v, style = MonospaceCodeStyle, color = ToxicTextPrimary)
                                }
                            }
                            if (r.headers.size > 15) {
                                Text(
                                    text = "… and ${r.headers.size - 15} more headers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ToxicTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Body Preview
                    ResultCard(
                        content = r.bodyPreview.ifEmpty { "(Empty response body)" },
                        label = "BODY PREVIEW ${if (r.isBodyTruncated) "(Truncated at 64KB)" else ""}",
                        infoBadge = r.contentType ?: "text",
                        testTag = "http_body_preview"
                    )
                }
                null -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
