package com.example.feature.utilities

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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.domain.usecase.UtilitiesUseCase
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.delay

enum class UtilTab(val label: String) {
    TIMESTAMP("Epoch / Time"),
    UUID_GEN("UUID v4"),
    QUERY_PARSER("URL Query Parser"),
    UNICODE_INFO("Unicode Inspector")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilitiesScreen(
    utilitiesUseCase: UtilitiesUseCase,
    onNavigateBack: () -> Unit,
    onRecordHistory: (summary: String, snippet: String) -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val clipboard = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(UtilTab.TIMESTAMP) }

    // Live clock
    var currentEpochSeconds by remember { mutableLongStateOf(System.currentTimeMillis() / 1000) }
    LaunchedEffect(Unit) {
        while (true) {
            currentEpochSeconds = System.currentTimeMillis() / 1000
            delay(1000)
        }
    }

    // Timestamp state
    var epochInput by remember { mutableStateOf(currentEpochSeconds.toString()) }
    var humanDateInput by remember { mutableStateOf("2026-10-11 12:00:00") }
    val conversionResult = remember(epochInput) {
        utilitiesUseCase.convertEpoch(epochInput)
    }

    // UUID state
    var uuidCount by remember { mutableIntStateOf(1) }
    var uuidUppercase by remember { mutableStateOf(false) }
    var uuidHyphens by remember { mutableStateOf(true) }
    var generatedUuids by remember {
        mutableStateOf(utilitiesUseCase.generateUuids(1, false, true))
    }

    // Query parser state
    var urlQueryInput by remember { mutableStateOf("https://example.com/api/v1/search?term=developer+tools&category=security&page=1&limit=20") }
    val parsedQueryParams = remember(urlQueryInput) {
        utilitiesUseCase.parseQueryString(urlQueryInput)
    }

    // Unicode inspector state
    var unicodeInput by remember { mutableStateOf("Toxic ⚡ 2026") }
    val unicodeChars = remember(unicodeInput) {
        utilitiesUseCase.inspectUnicodeChars(unicodeInput)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Local Utilities", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Timestamps, UUIDs & string helpers", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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

            // Sub Tab Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UtilTab.values().forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label, fontSize = 11.sp) },
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

            when (selectedTab) {
                UtilTab.TIMESTAMP -> {
                    // Live clock card
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CURRENT UNIX EPOCH", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                                Text("$currentEpochSeconds", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                            }
                            CyberButton(
                                text = "Use Current",
                                onClick = { epochInput = currentEpochSeconds.toString() },
                                isOutlined = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SectionHeader(title = "CONVERT UNIX TIMESTAMP", subtitle = "Seconds or Milliseconds")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = epochInput,
                        onValueChange = { epochInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("epoch_input_field"),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (conversionResult != null) {
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = ToxicBorder,
                            backgroundColor = ToxicCodeBg
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("CONVERTED DATES", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Local: ${conversionResult.localDateTime}", style = MonospaceCodeStyle, color = ToxicTextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("UTC: ${conversionResult.utcDateTime}", style = MonospaceCodeStyle, color = ToxicCyan)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ISO-8601: ${conversionResult.iso8601}", style = MonospaceCodeStyle, color = ToxicTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Epoch Millis: ${conversionResult.epochMillis}", style = MonospaceCodeStyle, color = ToxicTextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader(title = "DATE TO UNIX TIMESTAMP", subtitle = "Format: YYYY-MM-DD HH:MM:SS")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = humanDateInput,
                        onValueChange = { humanDateInput = it },
                        modifier = Modifier.fillMaxWidth(),
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

                    val convertedEpoch = utilitiesUseCase.parseDateToEpoch(humanDateInput)
                    if (convertedEpoch != null) {
                        ResultCard(
                            content = "Unix Epoch Seconds: $convertedEpoch\nUnix Epoch Millis: ${convertedEpoch * 1000L}",
                            label = "PARSED TIMESTAMP"
                        )
                    }
                }

                UtilTab.UUID_GEN -> {
                    SectionHeader(title = "UUID v4 GENERATOR", subtitle = "RFC 4122 compliant universally unique identifiers")
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Include Hyphens", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        Switch(
                            checked = uuidHyphens,
                            onCheckedChange = {
                                uuidHyphens = it
                                generatedUuids = utilitiesUseCase.generateUuids(uuidCount, uuidUppercase, uuidHyphens)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ToxicEmerald, checkedTrackColor = ToxicEmeraldDark)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Uppercase Characters", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        Switch(
                            checked = uuidUppercase,
                            onCheckedChange = {
                                uuidUppercase = it
                                generatedUuids = utilitiesUseCase.generateUuids(uuidCount, uuidUppercase, uuidHyphens)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ToxicEmerald, checkedTrackColor = ToxicEmeraldDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Batch Count", style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1, 3, 5, 10).forEach { c ->
                                FilterChip(
                                    selected = uuidCount == c,
                                    onClick = {
                                        uuidCount = c
                                        generatedUuids = utilitiesUseCase.generateUuids(c, uuidUppercase, uuidHyphens)
                                    },
                                    label = { Text("$c", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ToxicEmeraldDark,
                                        selectedLabelColor = ToxicEmerald,
                                        containerColor = ToxicSurface,
                                        labelColor = ToxicTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = "Generate UUID(s)",
                        onClick = {
                            generatedUuids = utilitiesUseCase.generateUuids(uuidCount, uuidUppercase, uuidHyphens)
                            onRecordHistory("Generated UUID", "${generatedUuids.size} UUID(s)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Filled.Refresh,
                        testTag = "generate_uuid_btn"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ResultCard(
                        content = generatedUuids.joinToString("\n"),
                        label = "GENERATED UUIDS",
                        infoBadge = "$uuidCount UUID(s)",
                        testTag = "uuid_output"
                    )
                }

                UtilTab.QUERY_PARSER -> {
                    SectionHeader(title = "URL QUERY STRING PARSER", subtitle = "Decompose parameter key-value pairs")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = urlQueryInput,
                        onValueChange = { urlQueryInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("query_url_field"),
                        textStyle = MonospaceCodeStyle,
                        placeholder = { Text("Paste URL or ?param1=val1&param2=val2...", style = MonospaceCodeStyle, color = ToxicTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ToxicBorderFocus,
                            unfocusedBorderColor = ToxicBorder,
                            focusedContainerColor = ToxicCodeBg,
                            unfocusedContainerColor = ToxicCodeBg,
                            focusedTextColor = ToxicTextPrimary,
                            unfocusedTextColor = ToxicTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DECODED PARAMETERS (${parsedQueryParams.size})", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (parsedQueryParams.isEmpty()) {
                                Text("(No query parameters found in input)", style = MonospaceCodeStyle, color = ToxicTextMuted)
                            } else {
                                parsedQueryParams.forEach { param ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(param.key, style = MonospaceCodeStyle, color = ToxicCyan)
                                        Text(param.value, style = MonospaceCodeStyle, color = ToxicTextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                UtilTab.UNICODE_INFO -> {
                    SectionHeader(title = "UNICODE CHARACTER INSPECTOR", subtitle = "Inspect code points, bytes & categories")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = unicodeInput,
                        onValueChange = { unicodeInput = it },
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = ToxicBorder,
                        backgroundColor = ToxicSurface
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("CHARACTERS BREAKDOWN", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                            Spacer(modifier = Modifier.height(8.dp))
                            unicodeChars.forEach { info ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CyberBadge(text = "'${info.char}'", color = ToxicEmerald, backgroundColor = ToxicEmeraldDark)
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Text(info.hexCodePoint, style = MonospaceCodeStyle, color = ToxicCyan)
                                    }
                                    Text(info.categoryName, style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
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
