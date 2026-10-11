package com.example.feature.encoding

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
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberButton
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.core.ui.WarningNoticeCard
import com.example.domain.usecase.EncodingUseCase
import com.example.domain.usecase.TransformResult
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary

enum class EncodingTab(val title: String) {
    BASE64("Base64"),
    URL("URL"),
    HEX("Hex"),
    BINARY("Binary & Radix")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncodingStudioScreen(
    encodingUseCase: EncodingUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(EncodingTab.BASE64) }

    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoBadge by remember { mutableStateOf<String?>(null) }
    var urlSafeBase64 by remember { mutableStateOf(false) }

    fun processOutcome(res: TransformResult, action: String) {
        when (res) {
            is TransformResult.Success -> {
                output = res.result
                infoBadge = res.info
                errorMessage = null
                onRecordHistory(action, input)
            }
            is TransformResult.Error -> {
                errorMessage = res.message
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Encoding Studio", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Base64, URL, Hex & Binary", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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
                            input = when (selectedTab) {
                                EncodingTab.BASE64 -> "Developer toolkit for Android 9+ & beyond!"
                                EncodingTab.URL -> "https://api.example.com/search?query=toxic toolbox&lang=en"
                                EncodingTab.HEX -> "Toxic Toolbox 2026"
                                EncodingTab.BINARY -> "Hello 123"
                            }
                            errorMessage = null
                        },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "encoding_sample_btn"
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

            // Radix / Encoding Mode Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EncodingTab.values().forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            errorMessage = null
                        },
                        label = { Text(tab.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToxicEmeraldDark,
                            selectedLabelColor = ToxicEmerald,
                            containerColor = ToxicSurface,
                            labelColor = ToxicTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "${selectedTab.title} Input", subtitle = "${input.length} characters")
                Row {
                    IconButton(
                        onClick = { clipboard.getText()?.text?.let { input = it } },
                        modifier = Modifier.testTag("paste_encoding_input")
                    ) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = ToxicTextSecondary)
                    }
                    IconButton(
                        onClick = {
                            input = ""
                            output = ""
                            errorMessage = null
                            infoBadge = null
                        },
                        modifier = Modifier.testTag("clear_encoding_input")
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
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("encoding_input_field"),
                placeholder = {
                    Text("Enter text or data to transform...", style = MonospaceCodeStyle, color = ToxicTextMuted)
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

            if (selectedTab == EncodingTab.BASE64) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("URL-safe Base64 (dash and underscore)", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                    Switch(
                        checked = urlSafeBase64,
                        onCheckedChange = { urlSafeBase64 = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ToxicEmerald,
                            checkedTrackColor = ToxicEmeraldDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTab) {
                    EncodingTab.BASE64 -> {
                        CyberButton(
                            text = "Encode Base64",
                            onClick = { processOutcome(encodingUseCase.base64Encode(input, urlSafeBase64), "Base64 Encode") },
                            modifier = Modifier.weight(1f),
                            testTag = "b64_encode_btn"
                        )
                        CyberButton(
                            text = "Decode Base64",
                            onClick = { processOutcome(encodingUseCase.base64Decode(input, urlSafeBase64), "Base64 Decode") },
                            modifier = Modifier.weight(1f),
                            isOutlined = true,
                            testTag = "b64_decode_btn"
                        )
                    }
                    EncodingTab.URL -> {
                        CyberButton(
                            text = "URL Encode",
                            onClick = { processOutcome(encodingUseCase.urlEncode(input), "URL Encode") },
                            modifier = Modifier.weight(1f),
                            testTag = "url_encode_btn"
                        )
                        CyberButton(
                            text = "URL Decode",
                            onClick = { processOutcome(encodingUseCase.urlDecode(input), "URL Decode") },
                            modifier = Modifier.weight(1f),
                            isOutlined = true,
                            testTag = "url_decode_btn"
                        )
                    }
                    EncodingTab.HEX -> {
                        CyberButton(
                            text = "Hex Encode",
                            onClick = { processOutcome(encodingUseCase.hexEncode(input), "Hex Encode") },
                            modifier = Modifier.weight(1f),
                            testTag = "hex_encode_btn"
                        )
                        CyberButton(
                            text = "Hex Decode",
                            onClick = { processOutcome(encodingUseCase.hexDecode(input), "Hex Decode") },
                            modifier = Modifier.weight(1f),
                            isOutlined = true,
                            testTag = "hex_decode_btn"
                        )
                    }
                    EncodingTab.BINARY -> {
                        CyberButton(
                            text = "Text -> Binary",
                            onClick = { processOutcome(encodingUseCase.textToBinary(input), "Text to Binary") },
                            modifier = Modifier.weight(1f),
                            testTag = "text_to_bin_btn"
                        )
                        CyberButton(
                            text = "Binary -> Text",
                            onClick = { processOutcome(encodingUseCase.binaryToText(input), "Binary to Text") },
                            modifier = Modifier.weight(1f),
                            isOutlined = true,
                            testTag = "bin_to_text_btn"
                        )
                    }
                }

                if (output.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            val temp = input
                            input = output
                            output = temp
                        },
                        modifier = Modifier.testTag("swap_encoding_btn")
                    ) {
                        Icon(Icons.Filled.SwapVert, contentDescription = "Swap Input/Output", tint = ToxicEmerald)
                    }
                }
            }

            if (selectedTab == EncodingTab.BINARY) {
                Spacer(modifier = Modifier.height(8.dp))
                CyberButton(
                    text = "Convert Decimal Number (to Bin & Hex)",
                    onClick = { processOutcome(encodingUseCase.decimalToBinaryAndHex(input), "Decimal to Radix") },
                    modifier = Modifier.fillMaxWidth(),
                    isOutlined = true,
                    testTag = "dec_radix_btn"
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))
                WarningNoticeCard(message = errorMessage!!)
            }

            Spacer(modifier = Modifier.height(16.dp))

            ResultCard(
                content = output,
                label = "RESULT",
                infoBadge = infoBadge,
                testTag = "encoding_output"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
