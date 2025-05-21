package com.unibo.cyberopoli.data.repositories.game

import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

interface IGameRepository {
    suspend fun createOrGetGame(lobbyId: String, lobbyMembers: List<LobbyMember>)

    suspend fun joinGame(): GamePlayer

    suspend fun updatePlayerPoints(value: Int)

    suspend fun updatePlayerPoints(value: Int, ownerId: String)

    suspend fun updatePlayerPosition(pos: Int)

    suspend fun getGamePlayers(): List<GamePlayer>

    suspend fun setNextTurn(nextTurnPlayer: String)

    suspend fun addGameEvent(event: GameEvent): GameEvent?

    suspend fun getGameEvents(): List<GameEvent>

    suspend fun getGamesHistory(): List<GameHistory>
}
