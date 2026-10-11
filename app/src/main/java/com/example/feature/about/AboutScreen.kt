package com.example.feature.about

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberCard
import com.example.core.ui.SectionHeader
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("About & Privacy", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("TOXIC TOOLBOX Manifesto", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
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

            // Brand Hero
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ToxicEmerald.copy(alpha = 0.5f),
                backgroundColor = ToxicSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ToxicEmeraldDark)
                            .border(1.dp, ToxicEmerald, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TX",
                            color = ToxicEmeraldBright,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TOXIC TOOLBOX",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ToxicTextPrimary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Version 1.0.0 (Native Android)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ToxicEmerald
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "An all-in-one offline-first developer toolkit designed for maximum efficiency, privacy, and zero remote tracking. Built with Kotlin, Jetpack Compose, Material 3, and Room.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ToxicTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Architecture
            SectionHeader(title = "PRIVACY FIRST ARCHITECTURE", subtitle = "Zero accounts, zero trackers, zero cloud leaks")
            Spacer(modifier = Modifier.height(10.dp))

            val principles = listOf(
                Triple("100% Offline-First", "JSON Studio, Encoding, Hashing, Text Lab, JWT Inspector, Random Generator, and Timestamps operate 100% locally on your device without transmitting a single byte over the wire.", Icons.Filled.WifiOff),
                Triple("Strict Secret Protection", "Cryptographic tokens, passwords, and JWT payloads are explicitly excluded from activity logs and device persistence.", Icons.Filled.Lock),
                Triple("Isolated Network Tools", "HTTP and DNS tools only trigger network calls when explicitly invoked by you. They include strict SSRF guardrails that block private subnets, localhost, and cloud metadata IPs.", Icons.Filled.Security),
                Triple("Zero Tracking & Telemetry", "No analytics SDKs, no crash trackers, no third-party advertisements, and no user identifiers.", Icons.Filled.Shield)
            )

            principles.forEach { (title, desc, icon) ->
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ToxicBorder,
                    backgroundColor = ToxicSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = ToxicEmerald, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.titleSmall, color = ToxicTextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = ToxicTextSecondary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tech Stack & Credits
            SectionHeader(title = "TECHNICAL SPECIFICATIONS")
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ToxicBorder,
                backgroundColor = ToxicSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("UI Framework", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("Jetpack Compose (M3)", style = MaterialTheme.typography.bodySmall, color = ToxicTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Architecture", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("MVVM + Clean Domain UseCases", style = MaterialTheme.typography.bodySmall, color = ToxicTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Local Persistence", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("Android Room 2.7.0 + DataStore", style = MaterialTheme.typography.bodySmall, color = ToxicTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Networking & Probes", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("OkHttp 4.10 (Bounded 64KB)", style = MaterialTheme.typography.bodySmall, color = ToxicTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Device Target", style = MaterialTheme.typography.bodyMedium, color = ToxicTextSecondary)
                        Text("Android 9.0 (API 28)+ / Vivo Y19 Compatible", style = MaterialTheme.typography.bodySmall, color = ToxicCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
