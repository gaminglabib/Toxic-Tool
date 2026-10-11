package com.example.feature.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.ui.EmptyStateView
import com.example.core.ui.SectionHeader
import com.example.core.ui.ToolCard
import com.example.data.repository.ToolboxRepository
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicTextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    repository: ToolboxRepository,
    onOpenTool: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val favorites by repository.favorites.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Favorites", style = MaterialTheme.typography.titleLarge, color = ToxicTextPrimary)
                        Text("${favorites.size} pinned shortcuts", style = MaterialTheme.typography.labelSmall, color = ToxicEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ToxicBackground)
            )
        },
        containerColor = ToxicBackground,
        modifier = modifier
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Favorite,
                title = "No Favorites Pinned",
                description = "Tap the heart icon on any tool card in Explore or Home to pin it here for quick access.",
                actionButtonText = "Explore Tools",
                onActionClick = onNavigateToExplore,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("empty_favorites_view")
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    SectionHeader(
                        title = "PINNED DEVELOPER TOOLS",
                        subtitle = "Always ready at your fingertips"
                    )
                }

                items(favorites, key = { it.id }) { tool ->
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

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
