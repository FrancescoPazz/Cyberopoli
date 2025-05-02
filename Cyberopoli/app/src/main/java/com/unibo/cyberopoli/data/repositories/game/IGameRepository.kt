package com.unibo.cyberopoli.data.repositories.game

import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

interface IGameRepository {
    suspend fun createGame(lobbyId: String, lobbyMembers: List<LobbyMember>): Game

    suspend fun joinGame(game: Game, userId: String): GamePlayer?

    suspend fun getGamePlayers(matchId: String): List<GamePlayer>

    suspend fun setNextTurn(game: Game, nextTurn: String): Game?

    suspend fun addGameEvent(event: GameEvent): GameEvent?

    suspend fun getGameEvents(lobbyId: String, gameId: String): List<GameEvent>
}
