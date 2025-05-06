package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.LiveData
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.data.models.auth.AuthState
data class SettingsParams(
    val changeTheme: (Theme) -> Unit,
    val themeState: ThemeState,
    val updatePasswordWithOldPassword: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    val authState: LiveData<AuthState>,
    val logout: () -> Unit,
)

data class ThemeState(val theme: Theme)
