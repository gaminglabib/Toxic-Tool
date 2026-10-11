package com.example.feature.text

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.domain.usecase.DiffType
import com.example.domain.usecase.TextLabUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicRedDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicSurfaceVariant
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary

enum class TextLabTab(val label: String) {
    TRANSFORMS("Transforms & Stats"),
    DIFF_VIEWER("Diff Compare")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextLabScreen(
    textLabUseCase: TextLabUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current
    var currentTab by remember { mutableStateOf(TextLabTab.TRANSFORMS) }

    // Transforms state
    var textInput by remember { mutableStateOf("") }
    var textOutput by remember { mutableStateOf("") }
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    var matchCase by remember { mutableStateOf(true) }
    var useRegex by remember { mutableStateOf(false) }

    // Diff state
    var diffOriginal by remember { mutableStateOf("") }
    var diffModified by remember { mutableStateOf("") }

    val stats = remember(textInput) {
        textLabUseCase.calculateStats(textInput)
    }

    val diffSummary = remember(diffOriginal, diffModified) {
        textLabUseCase.computeDiff(diffOriginal, diffModified)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Text Laboratory", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Live stats, transforms & diffing", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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
                            if (currentTab == TextLabTab.TRANSFORMS) {
                                textInput = "Toxic Toolbox is an all-in-one developer companion.\nLine 2: clean and resilient code.\nLine 3: duplicate line test\nLine 3: duplicate line test"
                            } else {
                                diffOriginal = "host=127.0.0.1\nport=8080\nenable_tls=false\ntimeout=5000"
                                diffModified = "host=192.168.1.100\nport=8080\nenable_tls=true\ntimeout=10000\nretries=3"
                            }
                        },
                        isOutlined = true,
                        modifier = Modifier.padding(end = 8.dp),
                        testTag = "text_sample_btn"
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

            // Sub Tab Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextLabTab.values().forEach { tab ->
                    FilterChip(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        label = { Text(tab.label, fontSize = 12.sp) },
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

            if (currentTab == TextLabTab.TRANSFORMS) {
                // Live stats dashboard
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ToxicBorder,
                    backgroundColor = ToxicSurface
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "METRICS & COUNTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ToxicEmerald,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Characters: ${stats.characterCount}", style = MonospaceCodeStyle, color = ToxicTextPrimary)
                            Text("No Spaces: ${stats.characterCountNoSpaces}", style = MonospaceCodeStyle, color = ToxicTextPrimary)
                            Text("Words: ${stats.wordCount}", style = MonospaceCodeStyle, color = ToxicTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Lines: ${stats.lineCount}", style = MonospaceCodeStyle, color = ToxicTextSecondary)
                            Text("Whitespace: ${stats.whitespaceCount}", style = MonospaceCodeStyle, color = ToxicTextSecondary)
                            Text("Bytes: ${stats.byteSizeUtf8}", style = MonospaceCodeStyle, color = ToxicCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "SOURCE TEXT")
                    Row {
                        IconButton(onClick = { clipboard.getText()?.text?.let { textInput = it } }) {
                            Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = ToxicTextSecondary)
                        }
                        IconButton(onClick = {
                            textInput = ""
                            textOutput = ""
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = ToxicTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("text_input_field"),
                    placeholder = {
                        Text("Type or paste text to analyze & transform...", style = MonospaceCodeStyle, color = ToxicTextMuted)
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

                // Case conversions
                SectionHeader(title = "CASE CONVERSIONS")
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CyberButton(
                        text = "UPPER",
                        onClick = {
                            textOutput = textLabUseCase.toUpperCase(textInput)
                            onRecordHistory("Text UpperCase", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f),
                        testTag = "upper_btn"
                    )
                    CyberButton(
                        text = "lower",
                        onClick = {
                            textOutput = textLabUseCase.toLowerCase(textInput)
                            onRecordHistory("Text LowerCase", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f),
                        testTag = "lower_btn"
                    )
                    CyberButton(
                        text = "Title",
                        onClick = {
                            textOutput = textLabUseCase.toTitleCase(textInput)
                            onRecordHistory("Text TitleCase", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f),
                        testTag = "title_btn"
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CyberButton(
                        text = "camelCase",
                        onClick = {
                            textOutput = textLabUseCase.toCamelCase(textInput)
                            onRecordHistory("Text camelCase", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "snake_case",
                        onClick = {
                            textOutput = textLabUseCase.toSnakeCase(textInput)
                            onRecordHistory("Text snake_case", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "kebab-case",
                        onClick = {
                            textOutput = textLabUseCase.toKebabCase(textInput)
                            onRecordHistory("Text kebab-case", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Line & Whitespace Tools
                SectionHeader(title = "LINE & WHITESPACE OPS")
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CyberButton(
                        text = "Trim Whitespace",
                        onClick = {
                            textOutput = textLabUseCase.trimExtraWhitespace(textInput)
                            onRecordHistory("Text Trimmed", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "Dedupe Lines",
                        onClick = {
                            textOutput = textLabUseCase.removeDuplicateLines(textInput)
                            onRecordHistory("Text Deduped", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CyberButton(
                        text = "Sort Lines A-Z",
                        onClick = {
                            textOutput = textLabUseCase.sortLines(textInput, false)
                            onRecordHistory("Sort Lines A-Z", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "Sort Lines Z-A",
                        onClick = {
                            textOutput = textLabUseCase.sortLines(textInput, true)
                            onRecordHistory("Sort Lines Z-A", textInput)
                        },
                        isOutlined = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Find & Replace
                SectionHeader(title = "FIND & REPLACE")
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = findText,
                        onValueChange = { findText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Find...", style = MonospaceCodeStyle, color = ToxicTextMuted) },
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
                    OutlinedTextField(
                        value = replaceText,
                        onValueChange = { replaceText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Replace...", style = MonospaceCodeStyle, color = ToxicTextMuted) },
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
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Match Case", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = matchCase,
                            onCheckedChange = { matchCase = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ToxicEmerald, checkedTrackColor = ToxicEmeraldDark)
                        )
                    }

                    CyberButton(
                        text = "Replace All",
                        onClick = {
                            textOutput = textLabUseCase.findAndReplace(textInput, findText, replaceText, matchCase, useRegex)
                            onRecordHistory("Find & Replace", textInput)
                        },
                        testTag = "replace_all_btn"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ResultCard(
                    content = textOutput,
                    label = "TRANSFORMED RESULT",
                    infoBadge = "${textOutput.length} chars",
                    testTag = "text_lab_output"
                )

            } else {
                // Diff compare view
                SectionHeader(title = "COMPARE TEXTS", subtitle = "Line-by-line difference detection")
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Original (A)", style = MaterialTheme.typography.labelSmall, color = ToxicTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = diffOriginal,
                            onValueChange = { diffOriginal = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("diff_original_field"),
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
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modified (B)", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = diffModified,
                            onValueChange = { diffModified = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("diff_modified_field"),
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberBadge(text = "+${diffSummary.additions} Added", color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                    CyberBadge(text = "-${diffSummary.deletions} Removed", color = ToxicAccentRed, backgroundColor = ToxicRedDark)
                    CyberBadge(text = "${diffSummary.identical} Unchanged", color = ToxicTextSecondary, backgroundColor = ToxicSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(12.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ToxicBorder,
                    backgroundColor = ToxicCodeBg
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("DIFF RESULT", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (diffSummary.lines.isEmpty()) {
                            Text("(Enter texts to view differences)", style = MonospaceCodeStyle, color = ToxicTextMuted)
                        } else {
                            diffSummary.lines.forEach { line ->
                                val (bg, prefix, textColor) = when (line.type) {
                                    DiffType.ADDED -> Triple(ToxicEmeraldDark.copy(alpha = 0.3f), "+ ", ToxicEmerald)
                                    DiffType.REMOVED -> Triple(ToxicRedDark.copy(alpha = 0.3f), "- ", ToxicAccentRed)
                                    DiffType.SAME -> Triple(androidx.compose.ui.graphics.Color.Transparent, "  ", ToxicTextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bg)
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = "$prefix${line.text}",
                                        style = MonospaceCodeStyle,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
