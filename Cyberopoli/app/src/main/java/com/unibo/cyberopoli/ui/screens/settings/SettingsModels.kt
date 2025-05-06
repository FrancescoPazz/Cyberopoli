package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.LiveData
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.models.settings.ThemeState
import com.unibo.cyberopoli.data.models.theme.Theme

data class SettingsParams(
    val changeTheme: (Theme) -> Unit,
    val themeState: ThemeState,
    val updatePasswordWithOldPassword: (oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    val authState: LiveData<AuthState>,
    val logout: () -> Unit,
)