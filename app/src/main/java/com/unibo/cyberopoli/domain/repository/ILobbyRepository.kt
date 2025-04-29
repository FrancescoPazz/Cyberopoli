package com.unibo.cyberopoli.domain.repository

import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData

interface ILobbyRepository {
    suspend fun createOrGetLobby(lobbyId: String, host: UserData): String

    suspend fun joinLobby(lobbyId: String, member: LobbyMemberData)

    suspend fun fetchMembers(lobbyId: String): List<LobbyMemberData>

    suspend fun toggleReady(lobbyId: String, userId: String, isReady: Boolean): LobbyMemberData

    suspend fun leaveLobby(lobbyId: String, userId: String, isHost: Boolean)

    suspend fun startGame(lobbyId: String)
}
