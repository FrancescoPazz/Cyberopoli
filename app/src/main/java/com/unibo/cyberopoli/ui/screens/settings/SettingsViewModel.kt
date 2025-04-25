package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val state = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    fun changeTheme(newTheme: Theme) = viewModelScope.launch {
        repository.setTheme(newTheme)
    }

    fun updatePasswordWithOldPassword(
        oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit
    ) = viewModelScope.launch {}

}