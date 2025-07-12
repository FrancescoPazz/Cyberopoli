package com.unibo.cyberopoli.ui.navigation

import java.util.UUID
import java.util.Locale
import android.os.Build
import org.koin.compose.KoinContext
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import kotlinx.serialization.Serializable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalContext
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.ui.theme.CyberopoliTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.unibo.cyberopoli.data.models.auth.AuthState
import androidx.compose.runtime.livedata.observeAsState
import com.unibo.cyberopoli.ui.screens.scan.view.ScanScreen
import com.unibo.cyberopoli.ui.screens.auth.view.AuthScreen
import com.unibo.cyberopoli.ui.screens.game.view.GameScreen
import com.unibo.cyberopoli.ui.screens.home.view.HomeScreen
import com.unibo.cyberopoli.ui.screens.lobby.view.LobbyScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unibo.cyberopoli.ui.screens.auth.viewmodel.AuthParams
import com.unibo.cyberopoli.ui.screens.game.viewmodel.GameParams
import com.unibo.cyberopoli.ui.screens.home.viewmodel.HomeParams
import com.unibo.cyberopoli.ui.screens.scan.viewmodel.ScanParams
import com.unibo.cyberopoli.ui.screens.loading.view.LoadingScreen
import com.unibo.cyberopoli.ui.screens.profile.view.ProfileScreen
import com.unibo.cyberopoli.ui.screens.ranking.view.RankingScreen
import com.unibo.cyberopoli.ui.screens.settings.view.SettingScreen
import com.unibo.cyberopoli.ui.screens.lobby.viewmodel.LobbyParams
import com.unibo.cyberopoli.ui.screens.auth.viewmodel.AuthViewModel
import com.unibo.cyberopoli.ui.screens.game.viewmodel.GameViewModel
import com.unibo.cyberopoli.ui.screens.scan.viewmodel.ScanViewModel
import com.unibo.cyberopoli.ui.screens.lobby.viewmodel.LobbyViewModel
import com.unibo.cyberopoli.ui.screens.profile.viewmodel.ProfileParams
import com.unibo.cyberopoli.ui.screens.ranking.viewmodel.RankingParams
import com.unibo.cyberopoli.ui.screens.settings.viewmodel.SettingsParams
import com.unibo.cyberopoli.ui.screens.ranking.viewmodel.RankingViewModel
import com.unibo.cyberopoli.ui.screens.profile.viewmodel.ProfileViewModel
import com.unibo.cyberopoli.ui.screens.settings.viewmodel.SettingsViewModel

sealed interface CyberopoliRoute {
    @Serializable
    data object Auth : CyberopoliRoute

    @Serializable
    data object Scan : CyberopoliRoute

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
    val gameViewModel = koinViewModel<GameViewModel>()

    val authState = authViewModel.authState.observeAsState()
    val language = settingsViewModel.language.collectAsStateWithLifecycle()
    val themeState by settingsViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    KoinContext {
        LaunchedEffect(language.value) {
            val locale = Locale(language.value)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        CyberopoliTheme(
            darkTheme = when (themeState.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                Theme.System -> isSystemInDarkTheme()
            },
        ) {
            if (authState.value == null || authState.value == AuthState.Loading) {
                LoadingScreen()
                return@CyberopoliTheme
            }

            val startRoute = when (authState.value) {
                is AuthState.Authenticated -> {
                    LaunchedEffect(Unit) {
                        profileViewModel.getUser()
                    }
                    CyberopoliRoute.Home
                }
                else -> CyberopoliRoute.Auth
            }

            NavHost(
                navController = navController,
                startDestination = startRoute,
            ) {
                composable<CyberopoliRoute.Auth> {
                    AuthScreen(
                        navController,
                        AuthParams(
                            login = authViewModel::login,
                            signUp = authViewModel::signUp,
                            authState = authViewModel.authState.observeAsState(),
                            loginGoogleUser = authViewModel::loginGoogle,
                            loginAnonymously = authViewModel::loginAnonymously,
                            sendPasswordReset = authViewModel::sendPasswordReset,
                            sendOtp = authViewModel::sendOTPCode,
                        ),
                    )
                }
                composable<CyberopoliRoute.Scan> {
                    ScanScreen(
                        navController,
                        ScanParams(
                            setScannedValue = scanViewModel::setScannedValue,
                            authState = authState,
                        ),
                    )
                }
                composable<CyberopoliRoute.Settings> {
                    SettingScreen(
                        navController,
                        SettingsParams(
                            themeState = themeState,
                            logout = authViewModel::logout,
                            authState = authViewModel.authState,
                            changeTheme = settingsViewModel::changeTheme,
                            language = settingsViewModel.language,
                            changeLanguage = settingsViewModel::changeLanguage,
                        ),
                    )
                }
                composable<CyberopoliRoute.Home> {
                    HomeScreen(
                        navController,
                        HomeParams(
                            user = profileViewModel.user,
                            gameHistories = profileViewModel.gameHistories,
                            topAppsUsage = profileViewModel.topAppsUsage,
                        ),
                    )
                }
                composable<CyberopoliRoute.Profile> {
                    ProfileScreen(
                        navController,
                        ProfileParams(
                            user = profileViewModel.user,
                            changeAvatar = profileViewModel::changeAvatar,
                            updateUserInfo = profileViewModel::updateUserInfo,
                            updatePasswordWithOldPassword = profileViewModel::updatePasswordWithOldPassword,
                        ),
                    )
                }
                composable<CyberopoliRoute.Ranking> {
                    val rankingVm = koinViewModel<RankingViewModel>()
                    RankingScreen(
                        navController,
                        RankingParams(
                            rankingData = rankingVm.rankingUsers.observeAsState(),
                            user = profileViewModel.user,
                        ),
                    )
                }
                composable<CyberopoliRoute.Lobby> {
                    val isGuest = profileViewModel.user.value?.isGuest!!
                    val members by lobbyViewModel.members.observeAsState()
                    LobbyScreen(
                        navController,
                        LobbyParams(
                            lobbyId = UUID.nameUUIDFromBytes(
                                scanViewModel.scannedValue.value?.toByteArray()
                                    ?: throw IllegalStateException("Scanned value is null"),
                            ).toString(),
                            lobby = lobbyViewModel.lobby.observeAsState(),
                            lobbyAlreadyStarted = lobbyViewModel.lobbyAlreadyStarted,
                            isGuest = isGuest,
                            members = members ?: throw IllegalStateException("Members are null"),
                            leaveLobby = lobbyViewModel::leaveLobby,
                            isHost = lobbyViewModel::isHost,
                            toggleReady = lobbyViewModel::toggleReady,
                            startLobbyFlow = lobbyViewModel::startLobbyFlow,
                            allReady = lobbyViewModel.allReady.observeAsState(),
                            setInApp = lobbyViewModel::setInApp,
                            user = profileViewModel.user
                        ),
                    )
                }
                composable<CyberopoliRoute.Game> {
                    val lobby = lobbyViewModel.lobby.observeAsState()
                    val members = lobbyViewModel.members.observeAsState()
                    val game = gameViewModel.game.observeAsState()
                    val gameAction = gameViewModel.actionsPermitted.collectAsStateWithLifecycle()
                    val player = gameViewModel.player.observeAsState()
                    val players = gameViewModel.players.observeAsState()
                    val diceRoll = gameViewModel.diceRoll.collectAsStateWithLifecycle()
                    val dialogData = gameViewModel.dialog.collectAsStateWithLifecycle()
                    val turnIndex =
                        players.value?.indexOfFirst { p -> p.userId == game.value?.turn } ?: 0

                    GameScreen(
                        navController = navController,
                        gameParams = GameParams(
                            game = game,
                            lobby = lobby,
                            members = members,
                            player = player,
                            players = players,
                            diceRoll = diceRoll,
                            dialogData = dialogData,
                            gameAction = gameAction,
                            cells = gameViewModel.cells,
                            endTurn = gameViewModel::endTurn,
                            gameOver = gameViewModel.gameOver,
                            rollDice = gameViewModel::rollDice,
                            setInApp = lobbyViewModel::setInApp,
                            resetGame = gameViewModel::resetGame,
                            startGame = gameViewModel::startGame,
                            movePlayer = gameViewModel::movePlayer,
                            leaveLobby = lobbyViewModel::leaveLobby,
                            currentTurnIndex = derivedStateOf { turnIndex },
                            onResultDismiss = gameViewModel::onResultDismiss,
                            refreshUserData = profileViewModel::refreshUserData,
                            updatePlayerScore = gameViewModel::updatePlayerScore,
                            onDialogOptionSelected = gameViewModel::onDialogOptionSelected,
                            startAnimation = gameViewModel.startAnimation.collectAsStateWithLifecycle(),
                            isLoadingQuestion = gameViewModel.isLoadingQuestion.collectAsStateWithLifecycle(),
                            user = profileViewModel.user
                        ),
                    )
                }
            }
        }
    }
}
