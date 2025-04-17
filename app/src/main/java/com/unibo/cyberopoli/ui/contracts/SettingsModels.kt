package com.unibo.cyberopoli.ui.contracts

import com.unibo.cyberopoli.data.models.Theme
import com.unibo.cyberopoli.ui.screens.settings.ThemeState

data class SettingsParams(
    val changeTheme: (Theme) -> Unit,
    val themeState: ThemeState,
    val updatePasswordWithOldPassword: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    val authState: AuthState,
    val logout: () -> Unit,
)