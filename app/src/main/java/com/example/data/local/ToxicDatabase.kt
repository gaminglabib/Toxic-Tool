package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.FavoritesDao
import com.example.data.local.dao.HistoryDao
import com.example.data.local.dao.ToolUsageDao
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.HistoryEntity
import com.example.data.local.entity.ToolUsageEntity

@Database(
    entities = [
        HistoryEntity::class,
        FavoriteEntity::class,
        ToolUsageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ToxicDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun toolUsageDao(): ToolUsageDao

    companion object {
        @Volatile
        private var INSTANCE: ToxicDatabase? = null

        fun getInstance(context: Context): ToxicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToxicDatabase::class.java,
                    "toxic_toolbox.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
