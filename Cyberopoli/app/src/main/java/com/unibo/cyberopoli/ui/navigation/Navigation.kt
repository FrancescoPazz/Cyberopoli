package com.unibo.cyberopoli.ui.navigation

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
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.ui.screens.ar.ARScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthParams
import com.unibo.cyberopoli.ui.screens.auth.AuthScreen
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.ui.screens.game.GameParams
import com.unibo.cyberopoli.ui.screens.game.GameScreen
import com.unibo.cyberopoli.ui.screens.game.GameViewModel
import com.unibo.cyberopoli.ui.screens.home.HomeParams
import com.unibo.cyberopoli.ui.screens.home.HomeScreen
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CyberopoliNavGraph(navController: NavHostController) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val scanViewModel = koinViewModel<ScanViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val lobbyViewModel = koinViewModel<LobbyViewModel>()

    val authState = authViewModel.authState.observeAsState()
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
                            login = authViewModel::login,
                            signUp = authViewModel::signUp,
                            authState = authViewModel.authState.observeAsState(),
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
                            authState = authState.value ?: AuthState.Unauthenticated
                        )
                    )
                }
                composable<CyberopoliRoute.ARScreen> {
                    ARScreen(navController)
                }
                composable<CyberopoliRoute.Settings> {
                    SettingScreen(
                        navController, SettingsParams(
                            themeState = themeState,
                            logout = authViewModel::logout,
                            authState = authViewModel.authState,
                            changeTheme = settingsViewModel::changeTheme,
                        )
                    )
                }
                composable<CyberopoliRoute.Home> {
                    HomeScreen(
                        navController, HomeParams(
                            user = profileViewModel.user.observeAsState(),
                            gameHistories = profileViewModel.gameHistories.observeAsState(),
                        )
                    )
                }
                composable<CyberopoliRoute.Profile> {
                    ProfileScreen(
                        navController, ProfileParams(
                            user = profileViewModel.user.observeAsState(),
                            changeAvatar = profileViewModel::changeAvatar,
                            updateUserInfo = profileViewModel::updateUserInfo,
                            updatePasswordWithOldPassword = profileViewModel::updatePasswordWithOldPassword
                        )
                    )
                }
                composable<CyberopoliRoute.Ranking> {
                    val rankingVm = koinViewModel<RankingViewModel>()
                    RankingScreen(
                        navController, RankingParams(
                            rankingData = rankingVm.rankingUsers.observeAsState(),
                            user = profileViewModel.user.observeAsState(),
                        )
                    )
                }
                composable<CyberopoliRoute.Lobby> {
                    val isGuest = profileViewModel.user.value?.isGuest!!
                    val members by lobbyViewModel.members.observeAsState()
                    LobbyScreen(
                        navController, LobbyParams(
                            lobbyId = UUID.nameUUIDFromBytes(
                                scanViewModel.scannedValue.value?.toByteArray()
                                    ?: throw IllegalStateException("Scanned value is null")
                            ).toString(),
                            isGuest = isGuest,
                            members = members ?: throw IllegalStateException("Members are null"),
                            startGame = lobbyViewModel::startGame,
                            leaveLobby = lobbyViewModel::leaveLobby,
                            isHost = lobbyViewModel.isHost.observeAsState(),
                            toggleReady = lobbyViewModel::toggleReady,
                            startLobbyFlow = lobbyViewModel::startLobbyFlow,
                            allReady = lobbyViewModel.allReady.observeAsState()
                        )
                    )
                }
                composable<CyberopoliRoute.Game> {
                    val gameViewModel = koinViewModel<GameViewModel>()
                    val lobby = lobbyViewModel.lobby.observeAsState()
                    val members = lobbyViewModel.members.observeAsState()
                    val game = gameViewModel.game.observeAsState()
                    val gameAction = gameViewModel.actionsPermitted.collectAsStateWithLifecycle()
                    val players = gameViewModel.players.collectAsStateWithLifecycle()
                    val diceRoll = gameViewModel.diceRoll.collectAsStateWithLifecycle()
                    val dialogData = gameViewModel.dialog.collectAsStateWithLifecycle()
                    val turnIndex = players.value.indexOfFirst { p -> p.userId == game.value?.turn }
                    GameScreen(
                        navController = navController, gameParams = GameParams(
                            game = game,
                            lobby = lobby,
                            gameAction = gameAction,
                            members = members,
                            players = players,
                            diceRoll = diceRoll,
                            dialogData = dialogData,
                            endTurn = gameViewModel::endTurn,
                            rollDice = gameViewModel::rollDice,
                            startGame = gameViewModel::startGame,
                            movePlayer = gameViewModel::movePlayer,
                            leaveGame = lobbyViewModel::leaveLobby,
                            currentTurnIndex = derivedStateOf { turnIndex },
                            onResultDismiss = gameViewModel::onResultDismiss,
                            updatePlayerPoints = gameViewModel::updatePlayerPoints,
                            onDialogOptionSelected = gameViewModel::onDialogOptionSelected,
                            startAnimation = gameViewModel.startAnimation.collectAsStateWithLifecycle(),
                            animatedPositions = gameViewModel.animatedPositions.collectAsStateWithLifecycle(),
                            isLoadingQuestion = gameViewModel.isLoadingQuestion.collectAsStateWithLifecycle(),
                        )
                    )
                }
            }
        }
    }
}