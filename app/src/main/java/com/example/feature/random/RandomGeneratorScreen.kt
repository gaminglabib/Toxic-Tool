package com.example.feature.random

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.PrivacyNoticeBanner
import com.example.core.ui.ResultCard
import com.example.core.ui.SectionHeader
import com.example.domain.usecase.RandomGenConfig
import com.example.domain.usecase.RandomGeneratorUseCase
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomGeneratorScreen(
    randomGeneratorUseCase: RandomGeneratorUseCase,
    onNavigateBack: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    var length by remember { mutableFloatStateOf(24f) }
    var count by remember { mutableIntStateOf(1) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var excludeAmbiguous by remember { mutableStateOf(false) }

    var generatedResult by remember {
        mutableStateOf(
            randomGeneratorUseCase.generate(
                RandomGenConfig(
                    length = 24,
                    includeUppercase = true,
                    includeLowercase = true,
                    includeDigits = true,
                    includeSymbols = true,
                    excludeAmbiguous = false,
                    count = 1
                )
            )
        )
    }

    fun triggerGeneration() {
        generatedResult = randomGeneratorUseCase.generate(
            RandomGenConfig(
                length = length.toInt(),
                includeUppercase = useUppercase,
                includeLowercase = useLowercase,
                includeDigits = useDigits,
                includeSymbols = useSymbols,
                excludeAmbiguous = excludeAmbiguous,
                count = count
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Secure Random", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("Cryptographic string generator", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ToxicTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { triggerGeneration() }, modifier = Modifier.testTag("regenerate_btn")) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Regenerate", tint = ToxicEmerald)
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

            // Privacy Guarantee Notice
            PrivacyNoticeBanner(
                message = "Generated locally with java.security.SecureRandom. Never logged or saved to device history."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Length Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "TOKEN LENGTH", subtitle = "${length.toInt()} characters")
                CyberBadge(
                    text = "${String.format("%.1f", generatedResult.entropyBits)} bits entropy",
                    color = ToxicCyan,
                    backgroundColor = ToxicSurface
                )
            }

            Slider(
                value = length,
                onValueChange = {
                    length = it
                    triggerGeneration()
                },
                valueRange = 8f..96f,
                steps = 87,
                colors = SliderDefaults.colors(
                    thumbColor = ToxicEmerald,
                    activeTrackColor = ToxicEmerald,
                    inactiveTrackColor = ToxicEmeraldDark
                ),
                modifier = Modifier.testTag("length_slider")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Character Sets
            SectionHeader(title = "CHARACTER SETS")
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = useUppercase,
                    onClick = {
                        useUppercase = !useUppercase
                        triggerGeneration()
                    },
                    label = { Text("A-Z", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToxicEmeraldDark,
                        selectedLabelColor = ToxicEmerald,
                        containerColor = ToxicSurface,
                        labelColor = ToxicTextSecondary
                    )
                )
                FilterChip(
                    selected = useLowercase,
                    onClick = {
                        useLowercase = !useLowercase
                        triggerGeneration()
                    },
                    label = { Text("a-z", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToxicEmeraldDark,
                        selectedLabelColor = ToxicEmerald,
                        containerColor = ToxicSurface,
                        labelColor = ToxicTextSecondary
                    )
                )
                FilterChip(
                    selected = useDigits,
                    onClick = {
                        useDigits = !useDigits
                        triggerGeneration()
                    },
                    label = { Text("0-9", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToxicEmeraldDark,
                        selectedLabelColor = ToxicEmerald,
                        containerColor = ToxicSurface,
                        labelColor = ToxicTextSecondary
                    )
                )
                FilterChip(
                    selected = useSymbols,
                    onClick = {
                        useSymbols = !useSymbols
                        triggerGeneration()
                    },
                    label = { Text("!@#$", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ToxicEmeraldDark,
                        selectedLabelColor = ToxicEmerald,
                        containerColor = ToxicSurface,
                        labelColor = ToxicTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            FilterChip(
                selected = excludeAmbiguous,
                onClick = {
                    excludeAmbiguous = !excludeAmbiguous
                    triggerGeneration()
                },
                label = { Text("Exclude Ambiguous (l, 1, I, O, 0)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ToxicEmeraldDark,
                    selectedLabelColor = ToxicEmerald,
                    containerColor = ToxicSurface,
                    labelColor = ToxicTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Batch Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "BATCH QUANTITY", subtitle = "$count token(s)")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 3, 5, 10).forEach { c ->
                        FilterChip(
                            selected = count == c,
                            onClick = {
                                count = c
                                triggerGeneration()
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

            Spacer(modifier = Modifier.height(16.dp))

            CyberButton(
                text = "Generate New Token(s)",
                onClick = { triggerGeneration() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Refresh,
                testTag = "generate_token_btn"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results
            val combinedOutput = generatedResult.tokens.joinToString("\n")
            ResultCard(
                content = combinedOutput,
                label = "GENERATED SECRETS",
                infoBadge = generatedResult.strengthLabel,
                testTag = "random_output"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
