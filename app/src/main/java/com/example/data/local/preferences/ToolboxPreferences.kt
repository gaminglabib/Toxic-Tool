package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "toxic_settings")

class ToolboxPreferences(private val context: Context) {
    companion object {
        val KEY_HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
        val KEY_INDENT_SPACES = intPreferencesKey("indent_spaces")
        val KEY_AUTO_TRIM = booleanPreferencesKey("auto_trim")
        val KEY_THEME = stringPreferencesKey("theme_mode")
    }

    val isHistoryEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_HISTORY_ENABLED] ?: true
    }

    val indentSpaces: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_INDENT_SPACES] ?: 2
    }

    val autoTrim: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_TRIM] ?: false
    }

    suspend fun setHistoryEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HISTORY_ENABLED] = enabled
        }
    }

    suspend fun setIndentSpaces(spaces: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_INDENT_SPACES] = spaces
        }
    }

    suspend fun setAutoTrim(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_TRIM] = enabled
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
