package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolId: String,
    val toolName: String,
    val actionSummary: String,
    val snippet: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tool_favorites")
data class FavoriteEntity(
    @PrimaryKey
    val toolId: String,
    val isFavorite: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tool_usage")
data class ToolUsageEntity(
    @PrimaryKey
    val toolId: String,
    val count: Int = 1,
    val lastUsedAt: Long = System.currentTimeMillis()
)
