package com.unibo.cyberopoli.data.repositories.game

import java.util.UUID
import java.util.Locale
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import com.unibo.cyberopoli.util.UsageStatsHelper
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.lobby.Lobby
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.selectAsFlow
import com.unibo.cyberopoli.data.services.LLMService
import io.github.jan.supabase.postgrest.query.Columns
import com.unibo.cyberopoli.data.models.game.GameAsset
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import com.unibo.cyberopoli.data.repositories.auth.USERS_TABLE
import com.unibo.cyberopoli.data.repositories.lobby.LOBBY_TABLE
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.filter.FilterOperation

import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainGameRepository

const val GAME_TABLE = "games"
const val GAME_EVENTS_TABLE = "game_events"
const val GAME_ASSETS_TABLE = "game_assets"
const val GAME_PLAYERS_TABLE = "game_players"

class GameRepository(
    private val supabase: SupabaseClient,
    private val llmService: LLMService,
    private val usageStatsHelper: UsageStatsHelper,
) : DomainGameRepository {
    private val _lobbyStateFlow = MutableStateFlow<Lobby?>(null)    

    private val _gameStateFlow = MutableStateFlow<Game?>(null)
    override val currentGame: StateFlow<Game?> = _gameStateFlow

    private val _playerStateFlow = MutableStateFlow<GamePlayer?>(null)
    override val currentPlayer: StateFlow<GamePlayer?> = _playerStateFlow

    private val _playersStateFlow = MutableStateFlow<List<GamePlayer>>(emptyList())
    override val currentPlayers: StateFlow<List<GamePlayer>> = _playersStateFlow

    private val _gameEventsStateFlow = MutableStateFlow<List<GameEvent>>(emptyList())
    override val currentGameEvents: StateFlow<List<GameEvent>> = _gameEventsStateFlow

    private val _gameAssetsStateFlow = MutableStateFlow<List<GameAsset>>(emptyList())
    override val currentGameAssets: StateFlow<List<GameAsset>> = _gameAssetsStateFlow

    private val userCache = mutableMapOf<String, User>()

    companion object {
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    override suspend fun generateDigitalWellBeingStatements(): List<GameDialogData.HackerStatement> {
        val topApps =
            usageStatsHelper.getTopUsedApps().joinToString("; ") { "${it.first}:${it.second} h" }
        val totalSec = usageStatsHelper.getWeeklyUsageTime()
        val sessionStats = usageStatsHelper.getSessionStats()
        val sessionCount = sessionStats.sessionCount
        val avgSessionSec = sessionStats.averageSessionDuration / 1000
        val unlockCount = sessionStats.unlockCount
        val dataJson = """
            {
              "topApps": "$topApps",
              "totalUsageSec": $totalSec,
              "sessionCount": $sessionCount,
              "averageSessionSec": $avgSessionSec,
              "unlockCount": $unlockCount,
            }
            """.trimIndent()
        Log.d("TEST", "Data JSON: $dataJson")

        val currentLanguage = if (Locale.getDefault().language == "it") {
            "ITALIANO"
        } else {
            "INGLESE"
        }
        val systemPrompt = """
            Genera 5 domande di gioco (in $currentLanguage) basandoti su questi dati utente:
            $dataJson
            Cerca di diversificare il più possibile il contenuto delle domande.
            Devono sembrare gli imprevisti di Monopoli ma in chiave informatica.
            Ad esempio: 
            Hai usato per troppo tempo il telefono questa settimana, perdi 5 punti.
            
            Ovviamente devi fare tu le domande basandoti sui dati che ti ho dato.
            """.trimIndent()

        val raw = llmService.generate(
            model = "cyberopoli_model",
            prompt = systemPrompt,
            stream = false,
        )

        val cleaned = raw.trim().removePrefix("```json").removeSuffix("```").trim()

        val payloads: List<GameDialogData.HackerStatement> = try {
            jsonParser.decodeFromString(cleaned)
        } catch (e: Exception) {
            Log.e("TEST", "Failed to decode questions JSON: ${e.message}")
            emptyList()
        }

        Log.d("TEST", "Decoded $payloads")

        return payloads.map {
            GameDialogData.HackerStatement(
                titleRes = it.titleRes,
                contentRes = it.contentRes,
                points = it.points,
            )
        }
    }

    override suspend fun createOrGetGame(
        passedLobby: Lobby,
        lobbyMembers: List<LobbyMember>,
    ) {
        val lobbyId = passedLobby.id
        val newGame = Game(
            lobbyId = lobbyId,
            lobbyCreatedAt = passedLobby.createdAt,
            id = UUID.randomUUID().toString(),
            turn = lobbyMembers[0].userId,
        )
        try {
            var currentGame = supabase.from(GAME_TABLE).select {
                filter {
                    and {
                        eq("lobby_id", lobbyId)
                        eq("lobby_created_at", passedLobby.createdAt)
                    }

                }
            }.decodeSingleOrNull<Game>()

            if (currentGame == null) {
                currentGame = supabase.from(GAME_TABLE).insert(newGame) {
                    select()
                }.decodeSingle<Game>()

                supabase.from(LOBBY_TABLE)
                    .update(mapOf("status" to LobbyStatus.IN_PROGRESS.value)) {
                        filter {
                            and {
                                eq("id", lobbyId)
                                eq("created_at", passedLobby.createdAt)
                            }
                        }
                    }
            }
            val currentLobby = supabase.from(LOBBY_TABLE).select {
                filter {
                    and {
                        eq("id", lobbyId)
                        eq("created_at", passedLobby.createdAt)
                    }
                }
            }.decodeSingleOrNull<Lobby>()

            _lobbyStateFlow.value = currentLobby
            _gameStateFlow.value = currentGame

            observeGame()
            observeGamePlayers()
            observeGameEvents()
            observeGameAssets()
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGame() {
        val gameFlow: Flow<Game?> = supabase.from(GAME_TABLE).selectSingleValueAsFlow(Game::id) {
            eq("lobby_id", _gameStateFlow.value!!.lobbyId)
            eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
            eq("id", _gameStateFlow.value!!.id)
        }
        MainScope().launch {
            gameFlow.collect { rawGame ->
                if (rawGame != null) {
                    _gameStateFlow.value = rawGame
                } else {
                    _gameStateFlow.value = null
                }
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGamePlayers() {
        val gamePlayersFlow: Flow<List<GamePlayer>> =
            supabase.from(GAME_PLAYERS_TABLE).selectAsFlow(
                primaryKey = GamePlayer::userId,
                filter = FilterOperation(
                    "game_id",
                    FilterOperator.EQ,
                    _gameStateFlow.value!!.id,
                ),
            )
        MainScope().launch {
            var lastValid: List<GamePlayer> = emptyList()

            gamePlayersFlow.collect { rawPlayers ->

                if (rawPlayers.isNotEmpty()) {
                    lastValid = rawPlayers
                }

                val allIds = lastValid.map { it.userId }.distinct()
                val missingIds = allIds.filterNot { userCache.containsKey(it) }
                if (missingIds.isNotEmpty()) {
                    val fetchedUsers: List<User> = supabase.from("users").select {
                        filter { isIn("id", missingIds) }
                    }.decodeList<User>()

                    fetchedUsers.forEach { user ->
                        userCache[user.id] = user
                    }
                }

                val players = rawPlayers.map { r ->
                    GamePlayer(
                        lobbyId = r.lobbyId,
                        lobbyCreatedAt = r.lobbyCreatedAt,
                        gameId = r.gameId,
                        userId = r.userId,
                        score = r.score,
                        cellPosition = r.cellPosition,
                        round = r.round,
                        winner = r.winner,
                        user = userCache[r.userId]!!,
                    )
                }
                _playersStateFlow.value = players
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGameEvents() {
        val gameEventsFlow: Flow<List<GameEvent>> = supabase.from(GAME_EVENTS_TABLE).selectAsFlow(
            primaryKey = GameEvent::eventType,
            filter = FilterOperation(
                "game_id",
                FilterOperator.EQ,
                _gameStateFlow.value!!.id,
            ),
        )
        MainScope().launch {
            gameEventsFlow.collect { rawEvents ->
                if (rawEvents.isNotEmpty()) {
                    val events = rawEvents.map { r ->
                        GameEvent(
                            lobbyId = r.lobbyId,
                            lobbyCreatedAt = r.lobbyCreatedAt,
                            gameId = r.gameId,
                            senderUserId = r.senderUserId,
                            recipientUserId = r.recipientUserId,
                            eventType = r.eventType,
                            value = r.value,
                            createdAt = r.createdAt,
                        )
                    }
                    _gameEventsStateFlow.value = events
                }
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGameAssets() {
        val gameAssetsFlow: Flow<List<GameAsset>> = supabase.from(GAME_ASSETS_TABLE).selectAsFlow(
            primaryKey = GameAsset::cellId,
            filter = FilterOperation(
                "game_id",
                FilterOperator.EQ,
                _gameStateFlow.value!!.id,
            ),
        )
        MainScope().launch {
            gameAssetsFlow.collect { rawAssets ->
                if (rawAssets.isNotEmpty()) {
                    val assets = rawAssets.map { r ->
                        GameAsset(
                            lobbyId = r.lobbyId,
                            lobbyCreatedAt = r.lobbyCreatedAt,
                            gameId = r.gameId,
                            cellId = r.cellId,
                            ownerId = r.ownerId,
                            placedAtRound = r.placedAtRound,
                            expiresAtRound = r.expiresAtRound,
                        )
                    }
                    _gameAssetsStateFlow.value = assets
                }
            }
        }
    }

    override suspend fun joinGame(): GamePlayer? {
        if (_gameStateFlow.value == null) return null
        val session = supabase.auth.currentSessionOrNull() ?: return null

        val userId = session.user?.id
        try {
            val existingPlayer = supabase.from(GAME_PLAYERS_TABLE).select {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                        eq("user_id", userId!!)
                    }
                }
            }.decodeSingleOrNull<GamePlayer>()

            if (existingPlayer != null) {
                val player = GamePlayer(
                    lobbyId = existingPlayer.lobbyId,
                    lobbyCreatedAt = existingPlayer.lobbyCreatedAt,
                    gameId = existingPlayer.gameId,
                    userId = existingPlayer.userId,
                    score = existingPlayer.score,
                    cellPosition = existingPlayer.cellPosition,
                    round = existingPlayer.round,
                    winner = existingPlayer.winner,
                    user = existingPlayer.user,
                )
                _playerStateFlow.value = player
                return player
            }

            val toInsert = GamePlayer(
                lobbyId = _gameStateFlow.value!!.lobbyId,
                lobbyCreatedAt = _gameStateFlow.value!!.lobbyCreatedAt,
                gameId = _gameStateFlow.value!!.id,
                userId = userId!!,
                score = 50,
                cellPosition = 8,
                round = 1,
                winner = false,
            )
            val raw: GamePlayerRaw = supabase.from(GAME_PLAYERS_TABLE).insert(toInsert) {
                select(
                    Columns.raw(
                        """
                            *,
                            users (
                              *
                            )
                            """.trimIndent(),
                    ),
                )
            }.decodeSingle()
            val created = GamePlayer(
                lobbyId = raw.lobbyId,
                lobbyCreatedAt = raw.lobbyCreatedAt,
                gameId = raw.gameId,
                userId = raw.userId,
                score = raw.score,
                cellPosition = raw.cellPosition,
                round = raw.round,
                winner = raw.winner,
                user = raw.user,
            )
            _playerStateFlow.value = created
            return created
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun increasePlayerRound() {
        if (_playerStateFlow.value == null) throw Exception("No player found")
        try {
            val updatedPlayer = _playerStateFlow.value!!.copy(
                round = _playerStateFlow.value!!.round + 1,
            )
            Log.d("TESTEA", "Updating player: $updatedPlayer")
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }
            _playerStateFlow.value = updatedPlayer
        } catch (
            e: Exception, ) {
            throw e
        }
    }

    override suspend fun updatePlayerScore(value: Int) {
        updatePlayerScore(value, _playerStateFlow.value!!.userId)
    }

    override suspend fun updatePlayerScore(
        value: Int,
        ownerId: String,
    ) {
        if (_gameStateFlow.value == null) throw Exception("No game found")
        if (_playerStateFlow.value == null) throw Exception("No player found")

        try {
            val updatedPlayer = _playerStateFlow.value!!.copy(
                score = _playerStateFlow.value!!.score + value,
            )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }

            _playerStateFlow.value = updatedPlayer
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlayerPosition(pos: Int) {
        if (_gameStateFlow.value == null) throw Exception("No game found")
        if (_playerStateFlow.value == null) throw Exception("No player found")

        try {
            val updatedPlayer = _playerStateFlow.value!!.copy(
                cellPosition = pos,
            )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }

            _playerStateFlow.value = updatedPlayer
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGamePlayers(): List<GamePlayer> {
        if (_gameStateFlow.value == null) throw Exception("No game found")

        try {
            val raw: List<GamePlayerRaw> = supabase.from(GAME_PLAYERS_TABLE).select(
                Columns.raw(
                    """
                        *,
                        users (
                          id,
                          username,
                          name,
                          surname,
                          avatar_url
                        )
                        """.trimIndent(),
                ),
            ) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                    }
                }
            }.decodeList()
            val gamePlayers = raw.map { r ->
                val u = r.user
                GamePlayer(
                    lobbyId = r.lobbyId,
                    lobbyCreatedAt = r.lobbyCreatedAt,
                    gameId = r.gameId,
                    userId = r.userId,
                    score = r.score,
                    cellPosition = r.cellPosition,
                    round = r.round,
                    winner = r.winner,
                    user = u,
                )
            }

            _playersStateFlow.value = gamePlayers

            return gamePlayers
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun setNextTurn(nextTurnPlayer: String) {
        if (_gameStateFlow.value == null) throw Exception("No game found")

        try {
            val updatedGame = _gameStateFlow.value!!.copy(turn = nextTurnPlayer)
            supabase.from(GAME_TABLE).update(updatedGame) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("id", _gameStateFlow.value!!.id)
                    }
                }
            }

            _gameStateFlow.value = updatedGame
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addGameEvent(event: GameEvent): GameEvent {
        try {
            val inserted: GameEvent = supabase.from(GAME_EVENTS_TABLE).insert(event) {
                select(
                    Columns.raw(
                        """
                            *,
                            users(
                              id,
                              username,
                              name,
                              surname,
                              avatar_url
                            )
                            """.trimIndent(),
                    ),
                )
            }.decodeSingle()
            return inserted
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeGameEvent(event: GameEvent) {
        try {
            supabase.from(GAME_EVENTS_TABLE).delete {
                filter {
                    and {
                        eq("lobby_id", event.lobbyId)
                        eq("lobby_created_at", event.lobbyCreatedAt)
                        eq("game_id", event.gameId)
                        eq("sender_user_id", event.senderUserId)
                        eq("event_type", event.eventType)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGameEvents(): List<GameEvent> {
        if (_gameStateFlow.value == null) throw Exception("No game found")

        try {
            val raw: List<GameEvent> = supabase.from(GAME_EVENTS_TABLE).select(
                Columns.raw(
                    """
                        lobby_id,
                        game_id,
                        sender_user_id,
                        recipient_user_id,
                        event_type,
                        created_at,
                        value,
                        users(
                          id,
                          username,
                          name,
                          surname,
                          avatar_url
                        )
                        """.trimIndent(),
                ),
            ) {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                    }
                }
            }.decodeList()
            return raw
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun addGameAsset(gameAsset: GameAsset): GameAsset {
        try {
            val inserted: GameAsset = supabase.from(GAME_ASSETS_TABLE).insert(gameAsset) {
                select()
            }.decodeSingle()
            return inserted
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeGameAsset(gameAsset: GameAsset) {
        try {
            supabase.from(GAME_ASSETS_TABLE).delete {
                filter {
                    and {
                        eq("lobby_id", gameAsset.lobbyId)
                        eq("lobby_created_at", gameAsset.lobbyCreatedAt)
                        eq("game_id", gameAsset.gameId)
                        eq("cell_id", gameAsset.cellId)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGameAssets(): List<GameAsset> {
        if (_gameStateFlow.value == null) throw Exception("No game found")

        try {
            val raw: List<GameAsset> = supabase.from(GAME_ASSETS_TABLE).select {
                filter {
                    and {
                        eq("lobby_id", _gameStateFlow.value!!.lobbyId)
                        eq("lobby_created_at", _gameStateFlow.value!!.lobbyCreatedAt)
                        eq("game_id", _gameStateFlow.value!!.id)
                    }
                }
            }.decodeList()
            return raw
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun getGamesHistory(): List<GameHistory> {
        try {
            val userId =
                supabase.auth.currentSessionOrNull()?.user?.id ?: throw Exception("No user found")

            val historyItems: List<GameHistory> = supabase.from("v_games_history").select {
                filter {
                    eq("user_id", userId)
                }
                order("lobby_created_at", Order.ASCENDING)
            }.decodeList<GameHistory>()

            return historyItems
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun gameOver() {
        try {
            val game = _gameStateFlow.value ?: return

            supabase.from(LOBBY_TABLE).update(mapOf("status" to LobbyStatus.FINISHED.value)) {
                filter {
                    and {
                        eq("id", game.lobbyId)
                        eq("created_at", game.lobbyCreatedAt)
                    }
                }
            }

            clearGameData()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveUserProgress() {
        if (_playerStateFlow.value == null) throw Exception("No player found")

        try {
            Log.d(
                "GameRepository",
                "Saving user progress for player: ${_playerStateFlow.value!!.userId}"
            )
            Log.d(
                "GameRepository",
                "Current players: ${supabase.auth.currentSessionOrNull()?.user?.id}"
            )
            val user =
                _playersStateFlow.value.find { it.userId == _playerStateFlow.value!!.userId }?.user
                    ?: throw Exception("User not found in current players")
            val winner = _playersStateFlow.value.maxByOrNull { it.score } ?: return

            val updatedUser = user.copy(
                totalScore = user.totalScore + _playerStateFlow.value!!.score,
                totalGames = user.totalGames + 1,
                totalWins = if (winner.userId == user.id) user.totalWins + 1 else user.totalWins,
            )
            supabase.from(USERS_TABLE).update(updatedUser) {
                filter { eq("id", user.id) }
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error saving user progress: ${e.message}")
            throw e
        }
    }

    override suspend fun clearGameData() {
        try {
            val game = _gameStateFlow.value ?: return
            val player = _playerStateFlow.value ?: return
            val winner = _playersStateFlow.value.maxByOrNull { it.score } ?: return

            val updatedPlayer = supabase.from(GAME_PLAYERS_TABLE)
                .update(mapOf("winner" to (winner.userId == player.userId))) {
                    filter {
                        eq("lobby_id", game.lobbyId)
                        eq("game_id", game.id)
                        eq("user_id", player.userId)
                    }
                    select()
                }.decodeSingleOrNull<GamePlayer>()

            _playerStateFlow.value = updatedPlayer

            _lobbyStateFlow.value = null
            _gameStateFlow.value = null
            _playersStateFlow.value = emptyList()
            _gameEventsStateFlow.value = emptyList()
            _gameAssetsStateFlow.value = emptyList()
        } catch (e: Exception) {
            Log.e("GameRepository", "Error clearing game data: ${e.message}")
            throw e
        }
    }
}
