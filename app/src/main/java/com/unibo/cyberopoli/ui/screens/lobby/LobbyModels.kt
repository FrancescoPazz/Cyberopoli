package com.unibo.cyberopoli.ui.screens.lobby

import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData

data class LobbyParams(
    val scannedLobbyId: String,
    val lobbyId: String?,
    val members: List<LobbyMemberData>,
    val isGuest: Boolean,
    val isHost: Boolean,
    val allReady: Boolean,
    val startLobbyFlow: (String) -> Unit,
    val toggleReady: () -> Unit,
    val leaveLobby: () -> Unit,
    val startGame: () -> Unit
)