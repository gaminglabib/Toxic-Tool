package com.example.data.repository

import com.example.data.local.ToxicDatabase
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.ToolUsageEntity
import com.example.data.local.preferences.ToolboxPreferences
import com.example.domain.model.ToolCategory
import com.example.domain.model.ToolItem
import com.example.domain.model.ToolRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ToolboxRepository(
    private val database: ToxicDatabase,
    val preferences: ToolboxPreferences
) {
    private val historyDao = database.historyDao()
    private val favoritesDao = database.favoritesDao()
    private val usageDao = database.toolUsageDao()

    val allTools: List<ToolItem> = ToolRegistry.ALL_TOOLS

    fun getToolsByCategory(category: ToolCategory): List<ToolItem> {
        return allTools.filter { it.category == category }
    }

    fun searchTools(query: String): List<ToolItem> {
        if (query.isBlank()) return allTools
        val trimmed = query.trim().lowercase()
        return allTools.filter { tool ->
            tool.name.lowercase().contains(trimmed) ||
                tool.shortDescription.lowercase().contains(trimmed) ||
                tool.category.title.lowercase().contains(trimmed) ||
                tool.tags.any { it.contains(trimmed) }
        }
    }

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun recordHistory(toolId: String, actionSummary: String, snippet: String) {
        val tool = ToolRegistry.findById(toolId)
        // Privacy rule: Never record sensitive secrets like passwords, tokens, or JWTs
        if (tool?.isSensitiveSecret == true) return
        val isEnabled = preferences.isHistoryEnabled.first()
        if (!isEnabled) return

        val boundedSnippet = if (snippet.length > 120) snippet.take(120) + "…" else snippet
        historyDao.insertHistory(
            HistoryEntity(
                toolId = toolId,
                toolName = tool?.name ?: toolId,
                actionSummary = actionSummary,
                snippet = boundedSnippet
            )
        )
        historyDao.trimOldHistory()
    }

    suspend fun deleteHistoryItem(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }

    val favorites: Flow<List<ToolItem>> = favoritesDao.getAllFavorites().map { favList ->
        val favIds = favList.filter { it.isFavorite }.map { it.toolId }.toSet()
        allTools.filter { it.id in favIds }
    }

    fun isFavorite(toolId: String): Flow<Boolean> = favoritesDao.isFavorite(toolId).map { it == true }

    suspend fun toggleFavorite(toolId: String) {
        val currentFavs = favoritesDao.getAllFavorites().first()
        val exists = currentFavs.any { it.toolId == toolId && it.isFavorite }
        if (exists) {
            favoritesDao.removeFavorite(toolId)
        } else {
            favoritesDao.setFavorite(FavoriteEntity(toolId = toolId, isFavorite = true))
        }
    }

    val recentlyUsed: Flow<List<ToolItem>> = usageDao.getRecentlyUsed().map { usageList ->
        usageList.mapNotNull { usage ->
            allTools.find { it.id == usage.toolId }
        }
    }

    val frequentlyUsed: Flow<List<ToolItem>> = usageDao.getFrequentlyUsed().map { usageList ->
        usageList.mapNotNull { usage ->
            allTools.find { it.id == usage.toolId }
        }
    }

    suspend fun recordToolOpened(toolId: String) {
        usageDao.recordUsage(toolId)
    }

    suspend fun resetAllUserData() {
        historyDao.clearAll()
        favoritesDao.clearFavorites()
        usageDao.clearUsage()
        preferences.clearAll()
    }
}
