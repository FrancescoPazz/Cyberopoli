package com.unibo.cyberopoli.domain.repository

import com.unibo.cyberopoli.domain.model.MatchPlayer

interface IMatchRepository {
    suspend fun createMatch(lobbyId: String): String

    suspend fun getMatchPlayers(matchId: String): List<MatchPlayer>

    suspend fun addPointEvent(matchId: String, userId: String, delta: Int)
}
