package com.unibo.cyberopoli.ui.screens.lobby

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.lobby.Lobby

data class LobbyParams(
    val lobby: State<Lobby?>,
    val joinLobby: (String, String) -> Unit,
    val observeLobby: (String) -> Unit,
    val leaveLobby: (String) -> Unit,
    val toggleReady: (String) -> Unit,
    val scannedLobbyId: String,
    val playerName: String,
    val startGame: (String) -> Unit,
    val deleteAnonymousUserAndSignOut: () -> Unit,
    val isGuest: Boolean,
)