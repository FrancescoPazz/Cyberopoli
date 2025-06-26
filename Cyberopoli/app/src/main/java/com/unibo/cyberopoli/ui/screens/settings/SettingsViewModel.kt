package com.unibo.cyberopoli.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.settings.ThemeState
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.data.repositories.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

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

    val language = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Locale.getDefault().language)

    fun changeLanguage(langCode: String) = viewModelScope.launch {
        Log.d("SettingsViewModel", "Changing language to: $langCode")
        settingsRepository.setLanguage(langCode)
    }
}
