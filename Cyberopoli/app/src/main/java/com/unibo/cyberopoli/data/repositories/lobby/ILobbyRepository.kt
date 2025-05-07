package com.unibo.cyberopoli.data.repositories.lobby

import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

interface ILobbyRepository {
    suspend fun createOrGetLobby(lobbyId: String, host: User)

    suspend fun joinLobby(member: LobbyMember)

    suspend fun fetchMembers(lobbyId: String): List<LobbyMember>

    suspend fun toggleReady(lobbyId: String, userId: String, isReady: Boolean): LobbyMember

    suspend fun leaveLobby(lobbyId: String, userId: String, isHost: Boolean)

    suspend fun startGame(lobbyId: String)
}
