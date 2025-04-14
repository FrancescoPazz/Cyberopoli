package com.unibo.cyberopoli.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unibo.cyberopoli.data.models.Theme
import com.unibo.cyberopoli.ui.screens.ar.ARScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthState
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.home.HomeScreen
import com.unibo.cyberopoli.ui.screens.lobby.LobbyScreen
import com.unibo.cyberopoli.ui.screens.lobby.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.ProfileScreen
import com.unibo.cyberopoli.ui.screens.profile.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.ranking.RankingScreen
import com.unibo.cyberopoli.ui.screens.ranking.RankingViewModel
import com.unibo.cyberopoli.ui.screens.scan.ScanScreen
import com.unibo.cyberopoli.ui.screens.settings.SettingScreen
import com.unibo.cyberopoli.ui.screens.settings.SettingsViewModel
import com.unibo.cyberopoli.ui.theme.CyberopoliTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface CyberopoliRoute {
    @Serializable
    data object Auth : CyberopoliRoute
    @Serializable
    data object Scan : CyberopoliRoute
    @Serializable
    data object ARScreen : CyberopoliRoute
    @Serializable
    data object Settings : CyberopoliRoute
    @Serializable
    data object Home : CyberopoliRoute
    @Serializable
    data object Profile : CyberopoliRoute
    @Serializable
    data object Ranking : CyberopoliRoute
    @Serializable
    data object Lobby : CyberopoliRoute
}

@Composable
fun CyberopoliNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    val lobbyViewModel = koinViewModel<LobbyViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val rankingViewModel = koinViewModel<RankingViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val themeState by settingsViewModel.state.collectAsStateWithLifecycle()
    val authState = authViewModel.authState.observeAsState()

    CyberopoliTheme(
        darkTheme = when (themeState.theme) {
            Theme.Light -> false
            Theme.Dark -> true
            Theme.System -> isSystemInDarkTheme()
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (authState.value == AuthState.Authenticated) CyberopoliRoute.Home else CyberopoliRoute.Auth,
        ) {
            composable<CyberopoliRoute.Auth> {
                AuthScreen(navController, authViewModel)
            }
            composable<CyberopoliRoute.Scan> {
                ScanScreen(navController, authViewModel)
            }
            composable<CyberopoliRoute.ARScreen> {
                ARScreen(navController)
            }
            composable<CyberopoliRoute.Settings> {
                SettingScreen(
                    navController,
                    themeState,
                    settingsViewModel,
                    authViewModel
                )
            }
            composable<CyberopoliRoute.Home> {
                HomeScreen(navController)
            }
            composable<CyberopoliRoute.Profile> {
                ProfileScreen(
                    navController,
                    profileViewModel
                )
            }
            composable<CyberopoliRoute.Ranking> {
                RankingScreen(navController, rankingViewModel)
            }
            composable<CyberopoliRoute.Lobby> {
                LobbyScreen(navController, "lobbyId1", lobbyViewModel, profileViewModel)
            }
        }
    }

}