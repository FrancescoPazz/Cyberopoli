package com.unibo.cyberopoli.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.game.GameParams
import com.unibo.cyberopoli.ui.screens.game.GameScreen
import com.unibo.cyberopoli.ui.screens.game.GameViewModel
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
import java.util.UUID

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

    @Serializable
    data object Game : CyberopoliRoute
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CyberopoliNavGraph(navController: NavHostController) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.authState.observeAsState()

    val scanViewModel = koinViewModel<ScanViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val lobbyViewModel = koinViewModel<LobbyViewModel>()
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
                composable<CyberopoliRoute.ARScreen> { ARScreen(navController) }
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
                    val rankingVm = koinViewModel<RankingViewModel>()
                    RankingScreen(
                        navController, RankingParams(
                            rankingData = rankingVm.ranking.observeAsState(),
                            loadUserData = rankingVm::loadUserData,
                            getMyRanking = rankingVm::getMyRanking
                        )
                    )
                }
                composable<CyberopoliRoute.Lobby> {
                    val members by lobbyViewModel.members.collectAsStateWithLifecycle()
                    val currentUserId = profileViewModel.user.value?.id
                    val isGuest = profileViewModel.user.value?.isGuest ?: false
                    val isHost = members.firstOrNull()?.userId == currentUserId
                    val allReady = members.isNotEmpty() && members.all { it.isReady }

                    LobbyScreen(
                        navController, LobbyParams(
                            lobbyId = UUID.nameUUIDFromBytes(
                                scanViewModel.scannedValue.value?.toByteArray() ?: "".toByteArray()
                            ).toString(),
                            members = members,
                            isGuest = isGuest,
                            isHost = isHost,
                            allReady = allReady,
                            startLobbyFlow = lobbyViewModel::startLobbyFlow,
                            toggleReady = lobbyViewModel::toggleReady,
                            leaveLobby = lobbyViewModel::leaveLobby,
                            startGame = lobbyViewModel::startGame
                        )
                    )
                }
                composable<CyberopoliRoute.Game> {
                    val gameViewModel = koinViewModel<GameViewModel>()
                    val lobbyId by lobbyViewModel.lobbyId.collectAsStateWithLifecycle(null)
                    val members by lobbyViewModel.members.collectAsStateWithLifecycle()
                    val gameState by gameViewModel.game.collectAsStateWithLifecycle()
                    val players by gameViewModel.players.collectAsStateWithLifecycle()
                    val turnIndex = players.indexOfFirst { p -> p.userId == gameState?.turn }
                    val phase by gameViewModel.phase.collectAsStateWithLifecycle()
                    val diceRoll by gameViewModel.diceRoll.collectAsStateWithLifecycle()
                    val dialogData by gameViewModel.dialog.collectAsStateWithLifecycle()

                    if (lobbyId == null || members.isEmpty()) {
                        LoadingScreen()
                    } else {
                        GameScreen(
                            navController = navController, gameParams = GameParams(
                                lobbyId = lobbyId ?: "",
                                lobbyMembers = members,
                                game = derivedStateOf { gameState },
                                players = derivedStateOf { players },
                                currentTurnIndex = derivedStateOf { turnIndex },
                                phase = derivedStateOf { phase },
                                diceRoll = derivedStateOf { diceRoll },
                                dialogData = derivedStateOf { dialogData },
                                startGame = gameViewModel::startGame,
                                rollDice = gameViewModel::rollDice,
                                movePlayer = gameViewModel::movePlayer,
                                onDialogOptionSelected = gameViewModel::onDialogOptionSelected,
                                onResultDismiss = gameViewModel::onResultDismiss,
                                leaveGame = lobbyViewModel::leaveLobby,
                                endTurn = gameViewModel::endTurn,
                                isLoadingQuestion = gameViewModel.isLoadingQuestion.collectAsStateWithLifecycle(),
                            )
                        )
                    }
                }
            }
        }
    }
}