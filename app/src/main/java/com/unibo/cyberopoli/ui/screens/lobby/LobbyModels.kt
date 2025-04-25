package com.unibo.cyberopoli.ui.screens.lobby

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.PlayerData

data class LobbyParams(
    val lobby: State<Lobby?>,
    val startLobbyFlow: (String) -> Unit,
    val leaveLobby: () -> Unit,
    val toggleReady: () -> Unit,
    val scannedLobbyId: String,
    val playerName: String,
    val startGame: () -> Unit,
    val deleteAnonymousUserAndSignOut: () -> Unit,
    val isGuest: Boolean,
    val players: List<PlayerData>?
)