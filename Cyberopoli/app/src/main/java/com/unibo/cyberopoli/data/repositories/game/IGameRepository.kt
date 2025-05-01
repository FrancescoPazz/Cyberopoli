package com.unibo.cyberopoli.data.repositories.game

import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer

interface IGameRepository {
    suspend fun createGame(lobbyId: String): Game

    suspend fun getGamePlayers(matchId: String): List<GamePlayer>

    suspend fun addPointEvent(matchId: String, userId: String, delta: Int)
}
