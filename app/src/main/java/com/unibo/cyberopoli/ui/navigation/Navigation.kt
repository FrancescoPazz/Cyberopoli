package com.unibo.cyberopoli.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.ui.screens.ar.ARScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthParams
import com.unibo.cyberopoli.ui.screens.auth.AuthScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthState
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.home.HomeParams
import com.unibo.cyberopoli.ui.screens.home.HomeScreen
import com.unibo.cyberopoli.ui.screens.home.HomeViewModel
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.LobbyParams
import com.unibo.cyberopoli.ui.screens.lobby.LobbyScreen
import com.unibo.cyberopoli.ui.screens.lobby.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.ProfileParams
import com.unibo.cyberopoli.ui.screens.profile.ProfileScreen
import com.unibo.cyberopoli.ui.screens.profile.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.ranking.RankingParams
import com.unibo.cyberopoli.ui.screens.ranking.RankingScreen
import com.unibo.cyberopoli.ui.screens.ranking.RankingViewModel
import com.unibo.cyberopoli.ui.screens.scan.ScanParams
import com.unibo.cyberopoli.ui.screens.scan.ScanScreen
import com.unibo.cyberopoli.ui.screens.scan.ScanViewModel
import com.unibo.cyberopoli.ui.screens.settings.SettingScreen
import com.unibo.cyberopoli.ui.screens.settings.SettingsParams
import com.unibo.cyberopoli.ui.screens.settings.SettingsViewModel
import com.unibo.cyberopoli.ui.theme.CyberopoliTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

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
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.authState.observeAsState()

    val scanViewModel = koinViewModel<ScanViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val themeState by settingsViewModel.state.collectAsStateWithLifecycle()

    KoinContext {
        CyberopoliTheme(
            darkTheme = when (themeState.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                Theme.System -> isSystemInDarkTheme()
            }
        ) {
            if (authState.value == null || authState.value == AuthState.Loading) {
                LoadingScreen()
                return@CyberopoliTheme
            }

            val startRoute = when (authState.value) {
                is AuthState.Authenticated -> CyberopoliRoute.Home
                else -> CyberopoliRoute.Auth
            }

            NavHost(
                navController = navController,
                startDestination = startRoute,
            ) {
                composable<CyberopoliRoute.Auth> {
                    AuthScreen(
                        navController, AuthParams(
                            authState = authViewModel.authState,
                            login = authViewModel::login,
                            signUp = authViewModel::signUp,
                            loginGoogleUser = authViewModel::loginGoogle,
                            resetPassword = authViewModel::sendPasswordReset,
                            loginAnonymously = authViewModel::loginAnonymously
                        )
                    )
                }
                composable<CyberopoliRoute.Scan> {
                    ScanScreen(
                        navController, ScanParams(
                            setScannedValue = scanViewModel::setScannedValue,
                            authState = authViewModel.authState
                        )
                    )
                }
                composable<CyberopoliRoute.ARScreen> {
                    ARScreen(navController)
                }
                composable<CyberopoliRoute.Settings> {
                    SettingScreen(
                        navController, SettingsParams(
                            changeTheme = settingsViewModel::changeTheme,
                            themeState = themeState,
                            updatePasswordWithOldPassword = settingsViewModel::updatePasswordWithOldPassword,
                            authState = authViewModel.authState,
                            logout = authViewModel::logout
                        )
                    )
                }
                composable<CyberopoliRoute.Home> {
                    HomeScreen(
                        navController, HomeParams(
                            user = profileViewModel.user.observeAsState(),
                            loadUserData = homeViewModel::loadUserData
                        )
                    )
                }
                composable<CyberopoliRoute.Profile> {
                    ProfileScreen(
                        navController, ProfileParams(
                            user = profileViewModel.user.observeAsState(),
                            loadUserData = homeViewModel::loadUserData,
                            changeAvatar = profileViewModel::changeAvatar
                        )
                    )
                }
                composable<CyberopoliRoute.Ranking> {
                    val rankingViewModel = koinViewModel<RankingViewModel>()
                    RankingScreen(
                        navController, RankingParams(
                            rankingData = rankingViewModel.ranking.observeAsState(),
                            loadUserData = rankingViewModel::loadUserData,
                            getMyRanking = rankingViewModel::getMyRanking
                        )
                    )
                }
                composable<CyberopoliRoute.Lobby> {
                    val lobbyViewModel = koinViewModel<LobbyViewModel>()
                    LobbyScreen(
                        navController, LobbyParams(
                            lobby = lobbyViewModel.lobby.observeAsState(),
                            startLobbyFlow = lobbyViewModel::startLobbyFlow,
                            leaveLobby = lobbyViewModel::leaveLobby,
                            toggleReady = lobbyViewModel::toggleReady,
                            startGame = lobbyViewModel::startGame,
                            deleteAnonymousUserAndSignOut = authViewModel::deleteAnonymousUserAndSignOut,
                            scannedLobbyId = scanViewModel.scannedValue.value ?: "",
                            playerName = "${profileViewModel.user.value?.displayName}",
                            isGuest = profileViewModel.user.value?.isGuest ?: true,
                            players = lobbyViewModel.players.value,
                        )
                    )
                }
            }
        }
    }
}