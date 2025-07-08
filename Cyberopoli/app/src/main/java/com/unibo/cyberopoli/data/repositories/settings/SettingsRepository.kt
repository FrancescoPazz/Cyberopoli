package com.unibo.cyberopoli.data.repositories.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unibo.cyberopoli.data.models.theme.Theme
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.map
import java.util.Locale
import com.unibo.cyberopoli.data.repositories.settings.ISettingsRepository as DomainSettingsRepository

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val supabase: SupabaseClient,
) : DomainSettingsRepository {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: Exception) {
            Theme.System
        }
    }

    val language = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: Locale.getDefault().language
    }

    override suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.toString()
        }
    }

    suspend fun setLanguage(langCode: String) {
        dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = langCode
        }
    }
}
