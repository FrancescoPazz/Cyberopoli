package com.unibo.cyberopoli.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unibo.cyberopoli.data.models.Theme
import com.unibo.cyberopoli.ui.contracts.AuthState
import com.unibo.cyberopoli.ui.contracts.AuthParams
import com.unibo.cyberopoli.ui.contracts.HomeParams
import com.unibo.cyberopoli.ui.contracts.LobbyParams
import com.unibo.cyberopoli.ui.contracts.ProfileParams
import com.unibo.cyberopoli.ui.contracts.RankingParams
import com.unibo.cyberopoli.ui.contracts.ScanParams
import com.unibo.cyberopoli.ui.contracts.SettingsParams
import com.unibo.cyberopoli.ui.screens.ar.ARScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.home.HomeScreen
import com.unibo.cyberopoli.ui.screens.home.HomeViewModel
import com.unibo.cyberopoli.ui.screens.lobby.LobbyScreen
import com.unibo.cyberopoli.ui.screens.lobby.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.ProfileScreen
import com.unibo.cyberopoli.ui.screens.profile.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.ranking.RankingScreen
import com.unibo.cyberopoli.ui.screens.ranking.RankingViewModel
import com.unibo.cyberopoli.ui.screens.scan.ScanScreen
import com.unibo.cyberopoli.ui.screens.scan.ScanViewModel
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CyberopoliNavGraph(navController: NavHostController) {
    val context = LocalContext.current

    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.authState.observeAsState()

    val scanViewModel = koinViewModel<ScanViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val themeState by settingsViewModel.state.collectAsStateWithLifecycle()

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
                val authParams = AuthParams(
                    authState = authViewModel.authState,
                    login = { email, password -> authViewModel.login(context, email, password) },
                    signUp = { email, password, name, surname -> authViewModel.signUp(context, email, password, name, surname) },
                )
                AuthScreen(navController, authParams)
            }
            composable<CyberopoliRoute.Scan> {
                val scanParams = ScanParams(
                    setScannedValue = { value -> scanViewModel.setScannedValue(value) },
                    authState = authState.value!!,
                )
                ScanScreen(navController, scanParams)
            }
            composable<CyberopoliRoute.ARScreen> {
                ARScreen(navController)
            }
            composable<CyberopoliRoute.Settings> {
                val settingsParams = SettingsParams(
                    changeTheme = { theme -> settingsViewModel.changeTheme(theme) },
                    themeState = themeState,
                    updatePasswordWithOldPassword = { oldPassword, newPassword, onSuccess, onError ->
                        settingsViewModel.updatePasswordWithOldPassword(
                            oldPassword,
                            newPassword,
                            onSuccess,
                            onError
                        )
                    },
                    authState = authState.value!!,
                    logout = { authViewModel.logout() },
                )
                SettingScreen(
                    navController, settingsParams
                )
            }
            composable<CyberopoliRoute.Home> {
                val homeParams = HomeParams(
                    user = homeViewModel.user.observeAsState(),
                    loadUserData = { homeViewModel.loadUserData() },
                )
                HomeScreen(navController, homeParams)
            }
            composable<CyberopoliRoute.Profile> {
                val profileParams = ProfileParams(
                    user = profileViewModel.user.observeAsState(),
                    loadUserData = { profileViewModel.loadUserData() },
                    changeAvatar = { profileViewModel.changeAvatar() },
                )
                ProfileScreen(navController, profileParams)
            }
            composable<CyberopoliRoute.Ranking> {
                val rankingViewModel = koinViewModel<RankingViewModel>()
                val rankingParams = RankingParams(
                    rankingData = rankingViewModel.ranking.observeAsState(),
                    loadUserData = { rankingViewModel.loadUserData() },
                    getMyRanking = { rankingViewModel.getMyRanking() },
                )
                RankingScreen(navController, rankingParams)
            }
            composable<CyberopoliRoute.Lobby> {
                val lobbyViewModel = koinViewModel<LobbyViewModel>()
                val lobbyParams = LobbyParams(
                    lobby = lobbyViewModel.lobby.observeAsState(),
                    joinLobby = { lobbyId, playerName -> lobbyViewModel.joinLobby(lobbyId, playerName) },
                    observeLobby = { lobbyId -> lobbyViewModel.observeLobby(lobbyId) },
                    leaveLobby = { lobbyId -> lobbyViewModel.leaveLobby(lobbyId) },
                    toggleReady = { lobbyId -> lobbyViewModel.toggleReady(lobbyId) },
                    scannedLobbyId = scanViewModel.scannedValue.value ?: "",
                    playerName = "${profileViewModel.user.value?.name} ${profileViewModel.user.value?.surname}",
                )
                LobbyScreen(navController, lobbyParams)
            }
        }
    }
}