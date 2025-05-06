package com.unibo.cyberopoli.data.repositories.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unibo.cyberopoli.data.models.auth.AuthResponse
import com.unibo.cyberopoli.data.models.theme.Theme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import com.unibo.cyberopoli.data.repositories.settings.ISettingsRepository as DomainSettingsRepository


class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val supabase: SupabaseClient
) : DomainSettingsRepository {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: Exception) {
            Theme.System
        }
    }

    override suspend fun setTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.toString()
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        val email = supabase.auth.currentUserOrNull()?.email ?: return
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = oldPassword
        }
        supabase.auth.updateUser {
            password = newPassword
        }
    }

}