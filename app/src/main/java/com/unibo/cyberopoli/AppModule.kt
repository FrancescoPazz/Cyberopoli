package com.unibo.cyberopoli

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.unibo.cyberopoli.data.repositories.SettingsRepository
import com.unibo.cyberopoli.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    viewModel { SettingsViewModel(get()) }

    single { SettingsRepository(get()) }

    single { get<Context>().dataStore }
}