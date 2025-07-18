package com.unibo.cyberopoli.ui.screens.lobby.view.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.lobby.viewmodel.LobbyParams

@Composable
fun LobbyStarterEffects(
    navController: NavHostController,
    params: LobbyParams,
) {
    var hasJoined by remember { mutableStateOf(false) }

    LaunchedEffect(params.lobbyId) {
        if (!hasJoined && params.lobbyId.isNotBlank()) {
            params.startLobbyFlow(params.lobbyId, params.user.value!!)
            hasJoined = true
        }
    }

    LaunchedEffect(params.lobby.value?.status) {
        if (params.lobby.value != null && params.lobby.value!!.status == LobbyStatus.IN_PROGRESS.value) {
            navController.navigate(CyberopoliRoute.Game) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
