package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.LiveData
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.models.settings.ThemeState
import com.unibo.cyberopoli.data.models.theme.Theme
import kotlinx.coroutines.flow.StateFlow

data class SettingsParams(
    val changeTheme: (Theme) -> Unit,
    val themeState: ThemeState,
    val language: StateFlow<String>,
    val changeLanguage: (String) -> Unit,
    val authState: LiveData<AuthState>,
    val logout: () -> Unit,
)
