package com.unibo.cyberopoli.data.repositories.lobby

import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

interface ILobbyRepository {
    suspend fun createOrGetLobby(
        lobbyId: String,
        host: User,
    )

    suspend fun joinLobby(member: LobbyMember)

    suspend fun fetchMembers(): List<LobbyMember>

    suspend fun toggleReady(isReady: Boolean): LobbyMember

    suspend fun leaveLobby(isHost: Boolean)
}
