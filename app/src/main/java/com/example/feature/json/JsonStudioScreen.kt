package com.example.feature.json

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.core.ui.WarningNoticeCard
import com.example.domain.usecase.JsonResult
import com.example.domain.usecase.JsonUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonStudioScreen(
    jsonUseCase: JsonUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorSnippet by remember { mutableStateOf<String?>(null) }
    var indentSpaces by remember { mutableIntStateOf(2) }
    var infoBadge by remember { mutableStateOf<String?>(null) }

    val sampleJson = """{"project":"TOXIC TOOLBOX","version":"1.0","offline":true,"features":["JSON","Crypto","Network"]}"""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("JSON Studio", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Format, validate & minify", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ToxicTextPrimary
                        )
                    }
                },
                actions = {
                    CyberButton(
                        text = "Sample",
                        onClick = {
                            input = sampleJson
                            errorMessage = null
                        },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "json_sample_btn"
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

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "JSON INPUT", subtitle = "${input.length} chars")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            clipboard.getText()?.text?.let { input = it }
                        },
                        modifier = Modifier.testTag("paste_input_btn")
                    ) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = ToxicTextSecondary)
                    }
                    IconButton(
                        onClick = {
                            input = ""
                            output = ""
                            errorMessage = null
                            errorSnippet = null
                            infoBadge = null
                        },
                        modifier = Modifier.testTag("clear_input_btn")
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
                    .height(180.dp)
                    .testTag("json_input_field"),
                placeholder = {
                    Text(
                        "Paste or type JSON object {} or array [] here...",
                        style = MonospaceCodeStyle,
                        color = ToxicTextMuted
                    )
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

            Spacer(modifier = Modifier.height(12.dp))

            // Indentation & Primary operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = indentSpaces == 2,
                        onClick = { indentSpaces = 2 },
                        label = { Text("2 Spaces", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToxicEmeraldDark,
                            selectedLabelColor = ToxicEmerald,
                            containerColor = ToxicSurface,
                            labelColor = ToxicTextSecondary
                        )
                    )
                    FilterChip(
                        selected = indentSpaces == 4,
                        onClick = { indentSpaces = 4 },
                        label = { Text("4 Spaces", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ToxicEmeraldDark,
                            selectedLabelColor = ToxicEmerald,
                            containerColor = ToxicSurface,
                            labelColor = ToxicTextSecondary
                        )
                    )
                }

                if (output.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            val temp = input
                            input = output
                            output = temp
                        },
                        modifier = Modifier.testTag("swap_json_btn")
                    ) {
                        Icon(Icons.Filled.SwapVert, contentDescription = "Swap Input/Output", tint = ToxicEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberButton(
                    text = "Beautify",
                    onClick = {
                        when (val res = jsonUseCase.beautify(input, indentSpaces)) {
                            is JsonResult.Success -> {
                                output = res.formatted
                                errorMessage = null
                                errorSnippet = null
                                infoBadge = "${res.lineCount} lines, ${res.byteCount} B"
                                onRecordHistory("JSON Beautify", input)
                            }
                            is JsonResult.Error -> {
                                errorMessage = res.message
                                errorSnippet = res.errorSnippet
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "beautify_btn"
                )

                CyberButton(
                    text = "Minify",
                    onClick = {
                        when (val res = jsonUseCase.minify(input)) {
                            is JsonResult.Success -> {
                                output = res.formatted
                                errorMessage = null
                                errorSnippet = null
                                infoBadge = "Minified (${res.byteCount} B)"
                                onRecordHistory("JSON Minify", input)
                            }
                            is JsonResult.Error -> {
                                errorMessage = res.message
                                errorSnippet = res.errorSnippet
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isOutlined = true,
                    testTag = "minify_btn"
                )

                CyberButton(
                    text = "Validate",
                    onClick = {
                        when (val res = jsonUseCase.validate(input)) {
                            is JsonResult.Success -> {
                                output = "✓ JSON Syntax is perfectly valid!\nStructure: ${res.formatted}\nSize: ${res.byteCount} bytes\nLines: ${res.lineCount}"
                                errorMessage = null
                                errorSnippet = null
                                infoBadge = "VALID"
                                onRecordHistory("JSON Validated", input)
                            }
                            is JsonResult.Error -> {
                                errorMessage = res.message
                                errorSnippet = res.errorSnippet
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isOutlined = true,
                    testTag = "validate_btn"
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))
                WarningNoticeCard(
                    title = "SYNTAX ERROR",
                    message = buildString {
                        append(errorMessage)
                        if (!errorSnippet.isNullOrBlank()) {
                            append("\nNear snippet: …")
                            append(errorSnippet)
                            append("…")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ResultCard(
                content = output,
                label = "FORMATTED OUTPUT",
                infoBadge = infoBadge,
                testTag = "json_output"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
