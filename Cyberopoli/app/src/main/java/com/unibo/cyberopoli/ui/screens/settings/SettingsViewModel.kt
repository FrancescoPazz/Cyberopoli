package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.settings.ThemeState
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.data.repositories.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val state =
        settingsRepository.theme.map { ThemeState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ThemeState(Theme.System),
        )

    fun changeTheme(newTheme: Theme) =
        viewModelScope.launch {
            settingsRepository.setTheme(newTheme)
        }
}
