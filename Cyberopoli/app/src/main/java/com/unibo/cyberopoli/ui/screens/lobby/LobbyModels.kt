package com.unibo.cyberopoli.ui.screens.lobby

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class LobbyParams(
    val lobbyId: String,
    val isGuest: Boolean,
    val lobby: State<Lobby?>,
    val startGame: () -> Unit,
    val leaveLobby: () -> Unit,
    val isHost: State<Boolean?>,
    val toggleReady: () -> Unit,
    val allReady: State<Boolean?>,
    val members: List<LobbyMember>,
    val setInApp: (inApp: Boolean) -> Unit,
    val startLobbyFlow: (lobbyId: String) -> Unit,
)
