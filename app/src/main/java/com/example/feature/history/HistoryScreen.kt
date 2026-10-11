package com.example.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.ui.CyberBadge
import com.example.core.ui.CyberButton
import com.example.core.ui.CyberCard
import com.example.core.ui.EmptyStateView
import com.example.core.ui.PrivacyNoticeBanner
import com.example.core.ui.SectionHeader
import com.example.data.local.entity.HistoryEntity
import com.example.data.repository.ToolboxRepository
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    repository: ToolboxRepository,
    onOpenTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val historyList by repository.getAllHistory().collectAsState(initial = emptyList())
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Activity History", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("${historyList.size} logged events", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_history_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = "Clear History",
                                tint = ToxicAccentRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ToxicBackground)
            )
        },
        containerColor = ToxicBackground,
        modifier = modifier
    ) { paddingValues ->
        if (historyList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.History,
                title = "No Recent Activity",
                description = "Actions performed in tools like JSON Studio, Encoding, and Text Laboratory will be listed here for quick review.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("empty_history_view")
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("history_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PrivacyNoticeBanner(
                        message = "Only non-sensitive operations are logged. Generated passwords and decoded JWTs are never recorded."
                    )
                }

                item {
                    SectionHeader(
                        title = "LOCAL ACTIVITY LOG",
                        subtitle = "Stored locally on device"
                    )
                }

                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        formattedTime = dateFormat.format(Date(item.timestamp)),
                        onDelete = {
                            scope.launch { repository.deleteHistoryItem(item.id) }
                        },
                        onClick = { onOpenTool(item.toolId) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear All History?", color = ToxicTextPrimary) },
                text = { Text("This will permanently remove all activity entries from your local device database.", color = ToxicTextSecondary) },
                confirmButton = {
                    CyberButton(
                        text = "Clear All",
                        isDestructive = true,
                        onClick = {
                            scope.launch { repository.clearHistory() }
                            showClearConfirmDialog = false
                        },
                        testTag = "confirm_clear_history_btn"
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel", color = ToxicTextSecondary)
                    }
                },
                containerColor = ToxicSurface
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryEntity,
    formattedTime: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}"),
        onClick = onClick,
        borderColor = ToxicBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyberBadge(
                        text = item.toolName,
                        color = ToxicEmerald,
                        backgroundColor = ToxicBackground
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = item.actionSummary,
                        style = MaterialTheme.typography.titleSmall,
                        color = ToxicTextPrimary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_history_item_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = ToxicTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.snippet,
                style = MonospaceCodeStyle,
                color = ToxicTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = ToxicTextMuted
            )
        }
    }
}
