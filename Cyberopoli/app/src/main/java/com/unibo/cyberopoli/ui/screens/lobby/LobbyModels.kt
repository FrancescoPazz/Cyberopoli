package com.unibo.cyberopoli.ui.screens.lobby

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class LobbyParams(
    val lobbyId: String,
    val members: List<LobbyMember>,
    val isGuest: Boolean,
    val isHost: State<Boolean?>,
    val allReady: State<Boolean?>,
    val startLobbyFlow: (lobbyId: String) -> Unit,
    val toggleReady: () -> Unit,
    val leaveLobby: () -> Unit,
    val startGame: () -> Unit
)
