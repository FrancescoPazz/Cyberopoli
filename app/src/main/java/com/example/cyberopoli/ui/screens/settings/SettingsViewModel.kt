package com.example.cyberopoli.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ThemeState(val theme: Theme)

class SettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ThemeState(Theme.System))
    val state = _state.asStateFlow()

    fun changeTheme(newTheme: Theme) {
        _state.value = ThemeState(newTheme)
    }
}