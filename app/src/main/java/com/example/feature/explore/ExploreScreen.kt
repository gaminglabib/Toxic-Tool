package com.example.feature.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.EmptyStateView
import com.example.core.ui.SectionHeader
import com.example.core.ui.ToolCard
import com.example.data.repository.ToolboxRepository
import com.example.domain.model.ToolCategory
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    repository: ToolboxRepository,
    onOpenTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ToolCategory?>(null) }

    val favorites by repository.favorites.collectAsState(initial = emptyList())

    val tools = remember(searchQuery, selectedCategory) {
        val base = repository.searchTools(searchQuery)
        if (selectedCategory != null) {
            base.filter { it.category == selectedCategory }
        } else {
            base
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Explore Tools", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("All 10 developer studios & utilities", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ToxicBackground)
            )
        },
        containerColor = ToxicBackground,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("explore_screen_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_field"),
                    placeholder = { Text("Filter by name, tags, or function...", color = ToxicTextMuted) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = ToxicEmerald) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = ToxicTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ToxicBorderFocus,
                        unfocusedBorderColor = ToxicBorder,
                        focusedContainerColor = ToxicSurface,
                        unfocusedContainerColor = ToxicSurface,
                        focusedTextColor = ToxicTextPrimary,
                        unfocusedTextColor = ToxicTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ToxicEmeraldDark,
                                selectedLabelColor = ToxicEmerald,
                                containerColor = ToxicSurface,
                                labelColor = ToxicTextSecondary
                            )
                        )
                    }
                    items(ToolCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text(cat.title, fontSize = 12.sp) },
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

            item {
                SectionHeader(
                    title = "AVAILABLE TOOLS (${tools.size})",
                    subtitle = selectedCategory?.subtitle ?: "Complete suite ready offline"
                )
            }

            if (tools.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Filled.Search,
                        title = "No Tools Found",
                        description = "Try adjusting your search terms or selecting another category."
                    )
                }
            } else {
                items(tools, key = { it.id }) { tool ->
                    val isFav = favorites.any { it.id == tool.id }
                    ToolCard(
                        tool = tool,
                        isFavorite = isFav,
                        onFavoriteToggle = {
                            scope.launch { repository.toggleFavorite(tool.id) }
                        },
                        onClick = {
                            scope.launch { repository.recordToolOpened(tool.id) }
                            onOpenTool(tool.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
