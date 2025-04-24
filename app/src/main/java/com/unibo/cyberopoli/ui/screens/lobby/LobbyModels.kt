package com.unibo.cyberopoli.ui.screens.lobby

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.PlayerData

data class LobbyParams(
    val lobby: State<Lobby?>,
    val joinLobby: (String) -> Unit,
    val observeLobby: (String) -> Unit,
    val leaveLobby: () -> Unit,
    val toggleReady: (String) -> Unit,
    val scannedLobbyId: String,
    val playerName: String,
    val startGame: (String) -> Unit,
    val deleteAnonymousUserAndSignOut: () -> Unit,
    val isGuest: Boolean,
    val players: List<PlayerData>?
)