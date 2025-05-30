package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
    params: LobbyParams,
) {
    AppLifecycleTracker(
        AppLifecycleTrackerScreenContext.LOBBY,
        params.setInApp,
    ) {
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
        params.leaveLobby()
    }

    LobbyStarterEffects(
        navController = navController,
        params = params,
    )

    BackHandler {
        params.leaveLobby()
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopBar(navController) {
                params.leaveLobby()
                navController.popBackStack()
            }
        },
        bottomBar = {
            if (!params.isGuest) BottomBar(navController)
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
            if (params.lobbyId.isEmpty() || params.members.isEmpty()) {
                LoadingScreen()
            } else {
                LobbyContent(
                    members = params.members,
                    isHost = params.isHost,
                    allReady = params.allReady,
                    onToggleReadyClick = params.toggleReady,
                    onStartGameClick = {
                        params.startGame()
                        navController.navigate(CyberopoliRoute.Game) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onExitClick = {
                        params.leaveLobby()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
