package com.unibo.cyberopoli.data.repositories.lobby

import kotlinx.coroutines.flow.StateFlow
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyResponse

interface ILobbyRepository {
    val currentLobby: StateFlow<Lobby?>
    val currentLobbyMembers: StateFlow<List<LobbyMember>>

    suspend fun createOrGetLobby(
        lobbyId: String,
        host: User,
    ): LobbyResponse

    suspend fun joinLobby(member: LobbyMember)

    suspend fun fetchMembers(): List<LobbyMember>

    suspend fun toggleReady(isReady: Boolean): LobbyMember

    suspend fun leaveLobby(isHost: Boolean)
}
