package com.unibo.cyberopoli.ui.screens.lobby.composables

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.lobby.LobbyParams

@Composable
fun LobbyStarterEffects(
    navController: NavHostController,
    params: LobbyParams,
) {
    var hasJoined by remember { mutableStateOf(false) }

    LaunchedEffect(params.lobbyId) {
        Log.d("LobbyStarterEffects", "Lobby ID: ${params.lobbyId}")
        if (!hasJoined && params.lobbyId.isNotBlank()) {
            Log.d("LobbyStarterEffects", "Joining lobby with ID: ${params.lobbyId}")
            params.startLobbyFlow(params.lobbyId)
            hasJoined = true
        }
    }

    LaunchedEffect(params.lobby.value?.status) {
        if (params.lobby.value != null && params.lobby.value!!.status == LobbyStatus.IN_PROGRESS.value) {
            Log.d("LobbyStarterEffects", "Lobby già in corso, navigazione verso Game screen")
            navController.navigate(CyberopoliRoute.Game) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
