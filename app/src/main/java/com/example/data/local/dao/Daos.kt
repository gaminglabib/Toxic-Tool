package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.ToolUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM activity_history ORDER BY timestamp DESC LIMIT 100")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity): Long

    @Query("DELETE FROM activity_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM activity_history")
    suspend fun clearAll()

    @Query("DELETE FROM activity_history WHERE id NOT IN (SELECT id FROM activity_history ORDER BY timestamp DESC LIMIT 100)")
    suspend fun trimOldHistory()
}

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM tool_favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT isFavorite FROM tool_favorites WHERE toolId = :toolId")
    fun isFavorite(toolId: String): Flow<Boolean?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM tool_favorites WHERE toolId = :toolId")
    suspend fun removeFavorite(toolId: String)

    @Query("DELETE FROM tool_favorites")
    suspend fun clearFavorites()
}

@Dao
interface ToolUsageDao {
    @Query("SELECT * FROM tool_usage ORDER BY lastUsedAt DESC")
    fun getRecentlyUsed(): Flow<List<ToolUsageEntity>>

    @Query("SELECT * FROM tool_usage ORDER BY count DESC LIMIT 5")
    fun getFrequentlyUsed(): Flow<List<ToolUsageEntity>>

    @Query("SELECT * FROM tool_usage WHERE toolId = :toolId")
    suspend fun getUsage(toolId: String): ToolUsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsage(entity: ToolUsageEntity)

    @Transaction
    suspend fun recordUsage(toolId: String, timestamp: Long = System.currentTimeMillis()) {
        val existing = getUsage(toolId)
        if (existing != null) {
            upsertUsage(existing.copy(count = existing.count + 1, lastUsedAt = timestamp))
        } else {
            upsertUsage(ToolUsageEntity(toolId = toolId, count = 1, lastUsedAt = timestamp))
        }
    }

    @Query("DELETE FROM tool_usage")
    suspend fun clearUsage()
}
