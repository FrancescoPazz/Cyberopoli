package com.unibo.cyberopoli.data.repositories.game

import kotlinx.coroutines.flow.StateFlow
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameAsset
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.game.GameDialogData

interface IGameRepository {
    val currentGame: StateFlow<Game?>
    val currentPlayer: StateFlow<GamePlayer?>
    val currentPlayers: StateFlow<List<GamePlayer>>
    val currentGameEvents: StateFlow<List<GameEvent>>
    val currentGameAssets: StateFlow<List<GameAsset>>

    suspend fun generateDigitalWellBeingStatements(): List<GameDialogData.HackerStatement>

    suspend fun createOrGetGame(
        passedLobby: Lobby,
        lobbyMembers: List<LobbyMember>,
    )

    suspend fun joinGame(): GamePlayer?

    suspend fun increasePlayerRound()

    suspend fun updatePlayerScore(value: Int)

    suspend fun updatePlayerScore(
        value: Int,
        ownerId: String,
    )

    suspend fun updatePlayerPosition(pos: Int)

    suspend fun getGamePlayers(): List<GamePlayer>

    suspend fun setNextTurn(nextTurnPlayer: String)

    suspend fun addGameEvent(event: GameEvent): GameEvent?

    suspend fun removeGameEvent(event: GameEvent)

    suspend fun getGameEvents(): List<GameEvent>

    suspend fun addGameAsset(gameAsset: GameAsset): GameAsset

    suspend fun removeGameAsset(gameAsset: GameAsset)

    suspend fun getGameAssets(): List<GameAsset>

    suspend fun getGamesHistory(): List<GameHistory>

    suspend fun gameOver()

    suspend fun saveUserProgress()

    suspend fun clearGameData()
}
