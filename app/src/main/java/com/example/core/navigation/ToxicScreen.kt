package com.example.core.navigation

sealed class ToxicScreen(val route: String) {
    object Home : ToxicScreen("home")
    object Explore : ToxicScreen("explore")
    object Favorites : ToxicScreen("favorites")
    object History : ToxicScreen("history")
    object Settings : ToxicScreen("settings")
    object About : ToxicScreen("about")

    data class ToolDetail(val toolId: String) : ToxicScreen("tool/$toolId")
}

sealed class BottomTab(val screen: ToxicScreen, val title: String, val testTag: String) {
    object Home : BottomTab(ToxicScreen.Home, "Home", "tab_home")
    object Explore : BottomTab(ToxicScreen.Explore, "Explore", "tab_explore")
    object Favorites : BottomTab(ToxicScreen.Favorites, "Favorites", "tab_favorites")
    object History : BottomTab(ToxicScreen.History, "History", "tab_history")
    object Settings : BottomTab(ToxicScreen.Settings, "Settings", "tab_settings")

    companion object {
        val items = listOf(Home, Explore, Favorites, History, Settings)
    }
}
