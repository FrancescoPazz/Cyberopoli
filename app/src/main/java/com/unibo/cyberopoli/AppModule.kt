package com.unibo.cyberopoli

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.unibo.cyberopoli.data.repositories.AuthRepository
import com.unibo.cyberopoli.data.repositories.RankingRepository
import com.unibo.cyberopoli.data.repositories.SettingsRepository
import com.unibo.cyberopoli.data.repositories.UserRepository
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.home.HomeViewModel
import com.unibo.cyberopoli.ui.screens.lobby.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.ranking.RankingViewModel
import com.unibo.cyberopoli.ui.screens.scan.ScanViewModel
import com.unibo.cyberopoli.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { ScanViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RankingViewModel(get(), get()) }
    viewModel { LobbyViewModel(get()) }

    single { AuthRepository(get()) }
    single { SettingsRepository(get()) }
    single { UserRepository() }
    single { RankingRepository() }
    single { get<Context>().dataStore }
}