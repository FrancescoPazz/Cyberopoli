package com.unibo.cyberopoli.domain.repository

import com.unibo.cyberopoli.domain.model.LobbyMember
import com.unibo.cyberopoli.domain.model.User

interface ILobbyRepository {
    suspend fun createOrGetLobby(lobbyId: String, host: User): String

    suspend fun joinLobby(lobbyId: String, member: LobbyMember)

    suspend fun fetchMembers(lobbyId: String): List<LobbyMember>

    suspend fun toggleReady(lobbyId: String, userId: String, isReady: Boolean): LobbyMember

    suspend fun leaveLobby(lobbyId: String, userId: String, isHost: Boolean)

    suspend fun startGame(lobbyId: String)
}
