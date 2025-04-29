package com.unibo.cyberopoli.domain.repository

import com.unibo.cyberopoli.data.models.match.MatchPlayerData
import com.unibo.cyberopoli.ui.screens.match.Match

interface IMatchRepository {
    suspend fun createMatch(lobbyId: String): Match

    suspend fun getMatchPlayers(matchId: String): List<MatchPlayerData>

    suspend fun addPointEvent(matchId: String, userId: String, delta: Int)
}
