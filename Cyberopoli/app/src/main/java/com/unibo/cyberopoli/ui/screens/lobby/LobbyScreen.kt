package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.composables.LobbyContent

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    params: LobbyParams
) {
    var hasJoined by remember { mutableStateOf(false) }
    var suppressLeaveOnStop by remember { mutableStateOf(false) }

    LaunchedEffect(params.lobby) {
        if (params.lobby.value != null && params.lobby.value!!.status == LobbyStatus.IN_PROGRESS.value) {
            Log.d("TEST LobbyScreen", "Lobby is already in progress, navigating to Game screen")
            navController.navigate(CyberopoliRoute.Game) {
                launchSingleTop = true
                restoreState = true
            }

        }
    }

    LaunchedEffect(params.lobbyId) {
        if (!hasJoined && params.lobbyId.isNotBlank()) {
            params.startLobbyFlow(params.lobbyId)
            hasJoined = true
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
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
                        suppressLeaveOnStop = true
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
