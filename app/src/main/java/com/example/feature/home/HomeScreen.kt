package com.example.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
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
import com.example.core.ui.EmptyStateView
import com.example.core.ui.SectionHeader
import com.example.core.ui.ToolCard
import com.example.data.repository.ToolboxRepository
import com.example.domain.model.ToolCategory
import com.example.domain.model.ToolItem
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderFocus
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicSurfaceVariant
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: ToolboxRepository,
    onOpenTool: (String) -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<ToolCategory?>(null) }

    val favorites by repository.favorites.collectAsState(initial = emptyList())
    val recentlyUsed by repository.recentlyUsed.collectAsState(initial = emptyList())
    val frequentlyUsed by repository.frequentlyUsed.collectAsState(initial = emptyList())

    val filteredTools = remember(searchQuery, selectedCategoryFilter) {
        val searched = repository.searchTools(searchQuery)
        if (selectedCategoryFilter != null) {
            searched.filter { it.category == selectedCategoryFilter }
        } else {
            searched
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ToxicEmeraldDark)
                                .border(1.dp, ToxicEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TX",
                                color = ToxicEmeraldBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TOXIC TOOLBOX",
                                style = MaterialTheme.typography.titleMedium,
                                color = ToxicTextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "OFFLINE DEVELOPER SUITE",
                                style = MaterialTheme.typography.labelSmall,
                                color = ToxicEmerald,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenAbout, modifier = Modifier.testTag("about_icon_btn")) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About",
                            tint = ToxicTextSecondary
                        )
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
                .testTag("home_screen_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input"),
                    placeholder = {
                        Text("Search tools, hashes, formats...", color = ToxicTextMuted)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = ToxicEmerald)
                    },
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

            // Quick Category Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All Tools", fontSize = 12.sp) },
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
                            selected = selectedCategoryFilter == cat,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                            },
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

            // If user is searching or filtered, show matching results directly
            if (searchQuery.isNotEmpty() || selectedCategoryFilter != null) {
                item {
                    SectionHeader(
                        title = "MATCHING TOOLS (${filteredTools.size})",
                        subtitle = if (searchQuery.isNotEmpty()) "Query: '$searchQuery'" else selectedCategoryFilter?.subtitle
                    )
                }

                if (filteredTools.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = Icons.Filled.Search,
                            title = "No Matching Tools",
                            description = "No developer utilities match your search filter."
                        )
                    }
                } else {
                    items(filteredTools, key = { it.id }) { tool ->
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
            } else {
                // Default Home Layout: Favorites shortcuts, Recently used, Frequently used, and All tools
                if (favorites.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "FAVORITE SHORTCUTS",
                            subtitle = "Quick access to pinned tools"
                        )
                    }
                    items(favorites, key = { "fav_${it.id}" }) { tool ->
                        ToolCard(
                            tool = tool,
                            isFavorite = true,
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

                if (recentlyUsed.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "RECENTLY ACCESSED",
                            subtitle = "Tools opened in this session"
                        )
                    }
                    items(recentlyUsed.take(3), key = { "recent_${it.id}" }) { tool ->
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

                // All Tools Overview
                item {
                    SectionHeader(
                        title = "DEVELOPER TOOL SUITE",
                        subtitle = "Select any tool to launch its studio"
                    )
                }

                items(repository.allTools, key = { "all_${it.id}" }) { tool ->
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
