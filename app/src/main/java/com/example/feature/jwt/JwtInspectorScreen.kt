package com.example.feature.jwt

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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.PrivacyNoticeBanner
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.core.ui.WarningNoticeCard
import com.example.domain.usecase.JwtInspectionResult
import com.example.domain.usecase.JwtUseCase
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JwtInspectorScreen(
    jwtUseCase: JwtUseCase,
    onNavigateBack: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current
    var tokenInput by remember { mutableStateOf("") }

    val sampleJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyXzEyMzQ1NiIsIm5hbWUiOiJEZXZlbG9wZXIiLCJyb2xlcyI6WyJhZG1pbiIsImRldmVsb3BlciJdLCJpYXQiOjE3MDQ1MDAwMDAsImV4cCI6MTgwMDAwMDAwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

    val inspectionResult = remember(tokenInput) {
        if (tokenInput.isBlank()) null
        else jwtUseCase.decodeToken(tokenInput)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("JWT Inspector", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Local decode & claims inspection", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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
                        onClick = { tokenInput = sampleJwt },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "jwt_sample_btn"
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

            // Crucial Security & Privacy Banner
            WarningNoticeCard(
                title = "LOCAL DECODE ONLY — NO REMOTE VERIFICATION",
                message = "Decoding displays header & payload claims without validating signatures. A decoded token may be expired, forged, or altered. Tokens are processed locally on device and never sent to any server."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input token
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "ENCODED TOKEN", subtitle = "${tokenInput.length} chars")
                Row {
                    IconButton(onClick = { clipboard.getText()?.text?.let { tokenInput = it } }) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = ToxicTextSecondary)
                    }
                    IconButton(onClick = { tokenInput = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = ToxicTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = tokenInput,
                onValueChange = { tokenInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("jwt_token_field"),
                placeholder = {
                    Text("Paste header.payload.signature token...", style = MonospaceCodeStyle, color = ToxicTextMuted)
                },
                textStyle = MonospaceCodeStyle,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ToxicBorderFocus,
                    unfocusedBorderColor = ToxicBorder,
                    focusedContainerColor = ToxicCodeBg,
                    unfocusedContainerColor = ToxicCodeBg,
                    focusedTextColor = ToxicTextPrimary,
                    unfocusedTextColor = ToxicTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val res = inspectionResult) {
                null -> {
                    PrivacyNoticeBanner(
                        message = "Enter or paste a JSON Web Token above to inspect its header, payload claims, and validity."
                    )
                }
                is JwtInspectionResult.Error -> {
                    WarningNoticeCard(
                        title = "INVALID TOKEN FORMAT",
                        message = res.message
                    )
                }
                is JwtInspectionResult.Success -> {
                    // Summary status chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        res.algorithm?.let {
                            CyberBadge(text = "Alg: $it", color = ToxicEmeraldBright, backgroundColor = ToxicEmeraldDark)
                        }
                        if (res.isExpired != null) {
                            CyberBadge(
                                text = if (res.isExpired) "EXPIRED" else "ACTIVE",
                                color = if (res.isExpired) ToxicAccentRed else ToxicEmeraldBright,
                                backgroundColor = if (res.isExpired) ToxicRedDark else ToxicEmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Structured Claims Card
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "EXTRACTED CLAIMS",
                                style = MaterialTheme.typography.labelSmall,
                                color = ToxicEmerald
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            res.claims.forEach { claim ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = claim.key,
                                            style = MonospaceCodeStyle,
                                            color = ToxicCyan
                                        )
                                        if (claim.description != null) {
                                            Text(
                                                text = claim.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ToxicTextMuted
                                            )
                                        }
                                    }
                                    Text(
                                        text = claim.value,
                                        style = MonospaceCodeStyle,
                                        color = ToxicTextPrimary,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Decoded Header
                    ResultCard(
                        content = res.prettyHeader,
                        label = "HEADER (ALGORITHM & TYPE)",
                        infoBadge = "JSON",
                        testTag = "jwt_header_output"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Decoded Payload
                    ResultCard(
                        content = res.prettyPayload,
                        label = "PAYLOAD (CLAIMS)",
                        infoBadge = "JSON",
                        testTag = "jwt_payload_output"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Signature
                    ResultCard(
                        content = res.signatureHex.ifEmpty { "(No signature segment)" },
                        label = "SIGNATURE RAW DIGEST",
                        testTag = "jwt_signature_output"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
