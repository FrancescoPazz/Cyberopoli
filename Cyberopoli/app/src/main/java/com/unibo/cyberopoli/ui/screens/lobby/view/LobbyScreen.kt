package com.unibo.cyberopoli.ui.screens.lobby.view

import android.os.Build
import android.widget.Toast
import com.unibo.cyberopoli.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import com.unibo.cyberopoli.ui.components.TopBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import com.unibo.cyberopoli.ui.components.BottomBar
import androidx.compose.foundation.layout.fillMaxSize
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.components.AppLifecycleTracker
import com.unibo.cyberopoli.ui.screens.loading.view.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.viewmodel.LobbyParams
import com.unibo.cyberopoli.ui.components.AppLifecycleTrackerScreenContext
import com.unibo.cyberopoli.ui.screens.lobby.view.composables.LobbyContent
import com.unibo.cyberopoli.ui.screens.lobby.view.composables.LobbyStarterEffects

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    lobbyParams: LobbyParams,
) {
    val context = LocalContext.current

    AppLifecycleTracker(
        AppLifecycleTrackerScreenContext.LOBBY,
        lobbyParams.user.value!!,
        lobbyParams.setInApp,
    ) {
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
        lobbyParams.leaveLobby(lobbyParams.user.value!!)
    }

    LobbyStarterEffects(
        navController = navController,
        params = lobbyParams,
    )

    BackHandler {
        lobbyParams.leaveLobby(lobbyParams.user.value!!)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopBar(navController) {
                lobbyParams.leaveLobby(lobbyParams.user.value!!)
                navController.popBackStack()
            }
        },
        bottomBar = {
            if (!lobbyParams.isGuest) BottomBar(navController)
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
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
                    user = lobbyParams.user.value!!,
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
                        lobbyParams.leaveLobby(lobbyParams.user.value!!)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxSize(),
                    isReady = lobbyParams.members.find { it.userId == lobbyParams.user.value?.id }?.isReady
                        ?: false,
                )
            }
        }
    }
}
