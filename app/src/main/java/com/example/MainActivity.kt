package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.BottomTab
import com.example.core.navigation.ToxicScreen
import com.example.feature.about.AboutScreen
import com.example.feature.dns.DnsLookupScreen
import com.example.feature.encoding.EncodingStudioScreen
import com.example.feature.explore.ExploreScreen
import com.example.feature.favorites.FavoritesScreen
import com.example.feature.hashing.HashStudioScreen
import com.example.feature.history.HistoryScreen
import com.example.feature.home.HomeScreen
import com.example.feature.http.HttpInspectorScreen
import com.example.feature.json.JsonStudioScreen
import com.example.feature.jwt.JwtInspectorScreen
import com.example.feature.network.NetworkDiagnosticsScreen
import com.example.feature.random.RandomGeneratorScreen
import com.example.feature.settings.SettingsScreen
import com.example.feature.text.TextLabScreen
import com.example.feature.utilities.UtilitiesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ToxicToolboxApp

        setContent {
            MyApplicationTheme {
                ToxicToolboxRoot(app = app)
            }
        }
    }
}

@Composable
fun ToxicToolboxRoot(app: ToxicToolboxApp) {
    val repository = app.repository
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<ToxicScreen>(ToxicScreen.Home) }
    val backStack = remember { mutableStateListOf<ToxicScreen>() }

    fun navigateTo(destination: ToxicScreen) {
        if (destination != currentScreen) {
            backStack.add(currentScreen)
            currentScreen = destination
        }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.size - 1)
        } else if (currentScreen != ToxicScreen.Home) {
            currentScreen = ToxicScreen.Home
        }
    }

    val isTopLevel = when (currentScreen) {
        ToxicScreen.Home,
        ToxicScreen.Explore,
        ToxicScreen.Favorites,
        ToxicScreen.History,
        ToxicScreen.Settings -> true
        else -> false
    }

    // Predictive / system back handling
    BackHandler(enabled = backStack.isNotEmpty() || currentScreen != ToxicScreen.Home) {
        navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevel) {
                ToxicBottomNavigationBar(
                    currentScreen = currentScreen,
                    onTabSelected = { tabScreen ->
                        if (currentScreen != tabScreen) {
                            backStack.clear()
                            currentScreen = tabScreen
                        }
                    }
                )
            }
        },
        containerColor = ToxicBackground
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (val screen = currentScreen) {
            ToxicScreen.Home -> {
                HomeScreen(
                    repository = repository,
                    onOpenTool = { toolId -> navigateTo(ToxicScreen.ToolDetail(toolId)) },
                    onOpenAbout = { navigateTo(ToxicScreen.About) },
                    modifier = screenModifier
                )
            }
            ToxicScreen.Explore -> {
                ExploreScreen(
                    repository = repository,
                    onOpenTool = { toolId -> navigateTo(ToxicScreen.ToolDetail(toolId)) },
                    modifier = screenModifier
                )
            }
            ToxicScreen.Favorites -> {
                FavoritesScreen(
                    repository = repository,
                    onOpenTool = { toolId -> navigateTo(ToxicScreen.ToolDetail(toolId)) },
                    onNavigateToExplore = {
                        backStack.clear()
                        currentScreen = ToxicScreen.Explore
                    },
                    modifier = screenModifier
                )
            }
            ToxicScreen.History -> {
                HistoryScreen(
                    repository = repository,
                    onOpenTool = { toolId -> navigateTo(ToxicScreen.ToolDetail(toolId)) },
                    modifier = screenModifier
                )
            }
            ToxicScreen.Settings -> {
                SettingsScreen(
                    repository = repository,
                    onNavigateToAbout = { navigateTo(ToxicScreen.About) },
                    modifier = screenModifier
                )
            }
            ToxicScreen.About -> {
                AboutScreen(
                    onNavigateBack = { navigateBack() }
                )
            }
            is ToxicScreen.ToolDetail -> {
                when (screen.toolId) {
                    "json_studio" -> {
                        JsonStudioScreen(
                            jsonUseCase = app.jsonUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("json_studio", summary, snippet) }
                            }
                        )
                    }
                    "encoding_studio" -> {
                        EncodingStudioScreen(
                            encodingUseCase = app.encodingUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("encoding_studio", summary, snippet) }
                            }
                        )
                    }
                    "hash_studio" -> {
                        HashStudioScreen(
                            hashUseCase = app.hashUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("hash_studio", summary, snippet) }
                            }
                        )
                    }
                    "text_lab" -> {
                        TextLabScreen(
                            textLabUseCase = app.textLabUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("text_lab", summary, snippet) }
                            }
                        )
                    }
                    "random_gen" -> {
                        RandomGeneratorScreen(
                            randomGeneratorUseCase = app.randomGeneratorUseCase,
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    "jwt_inspector" -> {
                        JwtInspectorScreen(
                            jwtUseCase = app.jwtUseCase,
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    "http_inspector" -> {
                        HttpInspectorScreen(
                            httpInspectorUseCase = app.httpInspectorUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("http_inspector", summary, snippet) }
                            }
                        )
                    }
                    "dns_lookup" -> {
                        DnsLookupScreen(
                            dnsUseCase = app.dnsUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("dns_lookup", summary, snippet) }
                            }
                        )
                    }
                    "network_diag" -> {
                        NetworkDiagnosticsScreen(
                            networkUseCase = app.networkDiagnosticsUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("network_diag", summary, snippet) }
                            }
                        )
                    }
                    "local_utils" -> {
                        UtilitiesScreen(
                            utilitiesUseCase = app.utilitiesUseCase,
                            onNavigateBack = { navigateBack() },
                            onRecordHistory = { summary, snippet ->
                                scope.launch { repository.recordHistory("local_utils", summary, snippet) }
                            }
                        )
                    }
                    else -> {
                        // Fallback to home
                        HomeScreen(
                            repository = repository,
                            onOpenTool = { toolId -> navigateTo(ToxicScreen.ToolDetail(toolId)) },
                            onOpenAbout = { navigateTo(ToxicScreen.About) },
                            modifier = screenModifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToxicBottomNavigationBar(
    currentScreen: ToxicScreen,
    onTabSelected: (ToxicScreen) -> Unit
) {
    NavigationBar(
        containerColor = ToxicSurface,
        modifier = Modifier
            .border(width = 0.5.dp, color = ToxicBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("toxic_bottom_bar")
    ) {
        val tabs = listOf(
            Triple(BottomTab.Home, Icons.Filled.Home, "tab_home"),
            Triple(BottomTab.Explore, Icons.Filled.Explore, "tab_explore"),
            Triple(BottomTab.Favorites, Icons.Filled.Favorite, "tab_favorites"),
            Triple(BottomTab.History, Icons.Filled.History, "tab_history"),
            Triple(BottomTab.Settings, Icons.Filled.Settings, "tab_settings")
        )

        tabs.forEach { (tab, icon, testTag) ->
            val isSelected = currentScreen == tab.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        color = if (isSelected) ToxicEmerald else ToxicTextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ToxicBackground,
                    selectedTextColor = ToxicEmerald,
                    indicatorColor = ToxicEmerald,
                    unselectedIconColor = ToxicTextMuted,
                    unselectedTextColor = ToxicTextMuted
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
