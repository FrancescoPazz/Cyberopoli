package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.AppLifecycleTracker
import com.unibo.cyberopoli.ui.components.AppLifecycleTrackerScreenContext
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.composables.LobbyContent
import com.unibo.cyberopoli.ui.screens.lobby.composables.LobbyStarterEffects

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    lobbyParams: LobbyParams,
) {
    val context = LocalContext.current

    AppLifecycleTracker(
        AppLifecycleTrackerScreenContext.LOBBY,
        lobbyParams.setInApp,
    ) {
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
        lobbyParams.leaveLobby()
    }

    LobbyStarterEffects(
        navController = navController,
        params = lobbyParams,
    )

    BackHandler {
        lobbyParams.leaveLobby()
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopBar(navController) {
                lobbyParams.leaveLobby()
                navController.popBackStack()
            }
        },
        bottomBar = {
            if (!lobbyParams.isGuest) BottomBar(navController)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (lobbyParams.lobbyAlreadyStarted.value) {
                Toast.makeText(
                    navController.context,
                    context.getString(R.string.lobby_already_started),
                    Toast.LENGTH_SHORT,
                ).show()
                navController.navigate(CyberopoliRoute.Home) {
                    launchSingleTop = true
                    restoreState = true
                }
            } else if (lobbyParams.lobbyId.isEmpty() || lobbyParams.members.isEmpty()) {
                LoadingScreen()
            } else {
                LobbyContent(
                    members = lobbyParams.members,
                    isHost = lobbyParams.isHost,
                    allReady = lobbyParams.allReady,
                    onToggleReadyClick = lobbyParams.toggleReady,
                    onStartGameClick = {
                        navController.navigate(CyberopoliRoute.Game) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onExitClick = {
                        lobbyParams.leaveLobby()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxSize(),
                    isReady = lobbyParams.members.find { it.userId == lobbyParams.userId }?.isReady ?: false,
                )
            }
        }
    }
}
