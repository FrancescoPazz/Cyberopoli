package com.unibo.cyberopoli.ui.screens.lobby.viewmodel

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class LobbyParams(
    val lobbyId: String,
    val isGuest: Boolean,
    val user: State<User?>,
    val lobby: State<Lobby?>,
    val leaveLobby: (user: User) -> Unit,
    val isHost: (user: User?) -> Boolean,
    val toggleReady: (user: User) -> Unit,
    val allReady: State<Boolean?>,
    val members: List<LobbyMember>,
    val setInApp: (user: User, inApp: Boolean) -> Unit,
    val lobbyAlreadyStarted: State<Boolean>,
    val startLobbyFlow: (lobbyId: String, user: User) -> Unit,
)
