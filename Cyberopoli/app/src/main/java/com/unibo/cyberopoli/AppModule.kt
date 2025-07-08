package com.unibo.cyberopoli

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.unibo.cyberopoli.data.repositories.auth.AuthRepository
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.ranking.RankingRepository
import com.unibo.cyberopoli.data.repositories.settings.SettingsRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import com.unibo.cyberopoli.data.services.LLMService
import com.unibo.cyberopoli.ui.screens.auth.viewmodel.AuthViewModel
import com.unibo.cyberopoli.ui.screens.game.viewmodel.GameViewModel
import com.unibo.cyberopoli.ui.screens.lobby.viewmodel.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.viewmodel.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.ranking.viewmodel.RankingViewModel
import com.unibo.cyberopoli.ui.screens.scan.viewmodel.ScanViewModel
import com.unibo.cyberopoli.ui.screens.settings.viewmodel.SettingsViewModel
import com.unibo.cyberopoli.util.UsageStatsHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { ScanViewModel() }
    viewModel { SettingsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { RankingViewModel(get()) }
    viewModel { LobbyViewModel(get(), get()) }
    viewModel { GameViewModel(androidApplication(), get(), get(), get()) }

    single { UsageStatsHelper(get()) }
    single { LLMService() }

    single { AuthRepository(get()) }
    single { SettingsRepository(get(), get()) }
    single { UserRepository(get()) }
    single { RankingRepository(get()) }
    single { LobbyRepository(get()) }
    single { GameRepository(get(), get(), get()) }
    single { get<Context>().dataStore }
}
