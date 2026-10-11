package com.example.feature.hashing

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.core.ui.PrivacyNoticeBanner
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.domain.usecase.HashUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashStudioScreen(
    hashUseCase: HashUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current

    var input by remember { mutableStateOf("") }
    var uppercaseHex by remember { mutableStateOf(false) }
    var expectedHashInput by remember { mutableStateOf("") }

    val hashResult = remember(input, uppercaseHex) {
        hashUseCase.computeHashes(input, uppercaseHex)
    }

    val matchSha256 = remember(hashResult.sha256, expectedHashInput) {
        hashUseCase.verifyHash(hashResult.sha256, expectedHashInput)
    }
    val matchSha384 = remember(hashResult.sha384, expectedHashInput) {
        hashUseCase.verifyHash(hashResult.sha384, expectedHashInput)
    }
    val matchSha512 = remember(hashResult.sha512, expectedHashInput) {
        hashUseCase.verifyHash(hashResult.sha512, expectedHashInput)
    }
    val hasMatch = matchSha256 || matchSha384 || matchSha512

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hash Studio", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("SHA-256, 384, 512 & checksum verification", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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
                        onClick = {
                            input = "The quick brown fox jumps over the lazy dog"
                            onRecordHistory("Computed Hashes", input)
                        },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "hash_sample_btn"
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

            // Explanation notice
            PrivacyNoticeBanner(
                message = "Cryptographic hashes are mathematical one-way digests, not reversible encryption. They cannot be decrypted back into plain text."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "INPUT TEXT", subtitle = "${input.length} chars (${hashResult.byteLength} bytes)")
                Row {
                    IconButton(
                        onClick = { clipboard.getText()?.text?.let { input = it } },
                        modifier = Modifier.testTag("paste_hash_input")
                    ) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = ToxicTextSecondary)
                    }
                    IconButton(
                        onClick = {
                            input = ""
                            expectedHashInput = ""
                        },
                        modifier = Modifier.testTag("clear_hash_input")
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = ToxicTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    if (it.isNotEmpty()) {
                        onRecordHistory("Computed Hashes", it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("hash_input_field"),
                placeholder = {
                    Text("Type or paste text to compute cryptographic hashes...", style = MonospaceCodeStyle, color = ToxicTextMuted)
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uppercase Hexadecimal Output", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                Switch(
                    checked = uppercaseHex,
                    onCheckedChange = { uppercaseHex = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ToxicEmerald,
                        checkedTrackColor = ToxicEmeraldDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checksum Verification Input
            SectionHeader(title = "CHECKSUM VERIFIER", subtitle = "Compare against expected hash")
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = expectedHashInput,
                onValueChange = { expectedHashInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expected_hash_field"),
                placeholder = {
                    Text("Paste known checksum to verify match...", style = MonospaceCodeStyle, color = ToxicTextMuted)
                },
                trailingIcon = {
                    if (expectedHashInput.isNotBlank()) {
                        CyberBadge(
                            text = if (hasMatch) "✓ MATCH" else "✗ MISMATCH",
                            color = if (hasMatch) ToxicEmeraldBright else ToxicAccentRed,
                            backgroundColor = if (hasMatch) ToxicEmeraldDark else ToxicAccentRed.copy(alpha = 0.2f)
                        )
                    }
                },
                textStyle = MonospaceCodeStyle,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (hasMatch) ToxicEmerald else ToxicBorderFocus,
                    unfocusedBorderColor = ToxicBorder,
                    focusedContainerColor = ToxicCodeBg,
                    unfocusedContainerColor = ToxicCodeBg,
                    focusedTextColor = ToxicTextPrimary,
                    unfocusedTextColor = ToxicTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Outputs
            ResultCard(
                content = hashResult.sha256,
                label = "SHA-256 (32 BYTES / 256 BITS)",
                infoBadge = if (matchSha256) "MATCH" else "64 hex digits",
                testTag = "sha256_result"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResultCard(
                content = hashResult.sha384,
                label = "SHA-384 (48 BYTES / 384 BITS)",
                infoBadge = if (matchSha384) "MATCH" else "96 hex digits",
                testTag = "sha384_result"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResultCard(
                content = hashResult.sha512,
                label = "SHA-512 (64 BYTES / 512 BITS)",
                infoBadge = if (matchSha512) "MATCH" else "128 hex digits",
                testTag = "sha512_result"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
