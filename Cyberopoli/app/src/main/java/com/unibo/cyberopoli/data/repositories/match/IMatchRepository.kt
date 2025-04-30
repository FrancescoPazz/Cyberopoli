package com.unibo.cyberopoli.data.repositories.match

import com.unibo.cyberopoli.data.models.match.MatchPlayer
import com.unibo.cyberopoli.ui.screens.match.Match

interface IMatchRepository {
    suspend fun createMatch(lobbyId: String): Match

    suspend fun getMatchPlayers(matchId: String): List<MatchPlayer>

    suspend fun addPointEvent(matchId: String, userId: String, delta: Int)
}
