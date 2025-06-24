package com.unibo.cyberopoli.data.repositories.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.data.repositories.auth.USERS_TABLE
import com.unibo.cyberopoli.data.repositories.lobby.LOBBY_TABLE
import com.unibo.cyberopoli.data.services.LLMService
import com.unibo.cyberopoli.util.UsageStatsHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale
import java.util.UUID
import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainGameRepository

const val GAME_TABLE = "games"
const val GAME_EVENTS_TABLE = "game_events"
const val GAME_PLAYERS_TABLE = "game_players"

class GameRepository(
    private val supabase: SupabaseClient,
    private val llmService: LLMService,
    private val usageStatsHelper: UsageStatsHelper,
) : DomainGameRepository {
    private val currentLobbyLiveData: MutableLiveData<Lobby?> = MutableLiveData()
    val currentGameLiveData: MutableLiveData<Game?> = MutableLiveData()
    val currentPlayerLiveData: MutableLiveData<GamePlayer?> = MutableLiveData()
    val currentPlayersLiveData: MutableLiveData<List<GamePlayer>> = MutableLiveData(emptyList())

    private val userCache = mutableMapOf<String, User>()

    companion object {
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    suspend fun generateDigitalWellBeingStatements(): List<GameDialogData.HackerStatement> {
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
                title = it.title,
                content = it.content,
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

            currentLobbyLiveData.value = currentLobby
            currentGameLiveData.value = currentGame

            observeGame()
            observeGamePlayers()
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGame() {
        val gameFlow: Flow<Game?> = supabase.from(GAME_TABLE).selectSingleValueAsFlow(Game::id) {
            eq("lobby_id", currentGameLiveData.value!!.lobbyId)
            eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
            eq("id", currentGameLiveData.value!!.id)
        }
        MainScope().launch {
            gameFlow.collect { rawGame ->
                if (rawGame != null) {
                    currentGameLiveData.value = rawGame
                } else {
                    currentGameLiveData.value = null
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
                    currentGameLiveData.value!!.id,
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
                currentPlayersLiveData.postValue(players)
            }
        }
    }

    override suspend fun joinGame(): GamePlayer? {
        if (currentGameLiveData.value == null) return null
        val session = supabase.auth.currentSessionOrNull() ?: return null

        val userId = session.user?.id
        try {
            val existingPlayer = supabase.from(GAME_PLAYERS_TABLE).select {
                filter {
                    and {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
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
                currentPlayerLiveData.value = player
                return player
            }

            val toInsert = GamePlayer(
                lobbyId = currentGameLiveData.value!!.lobbyId,
                lobbyCreatedAt = currentGameLiveData.value!!.lobbyCreatedAt,
                gameId = currentGameLiveData.value!!.id,
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
            currentPlayerLiveData.value = created
            return created
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun increasePlayerRound() {
        if (currentPlayerLiveData.value == null) throw Exception("No player found")
        try {
            val updatedPlayer = currentPlayerLiveData.value!!.copy(
                round = currentPlayerLiveData.value!!.round + 1,
            )
            Log.d("TESTEA", "Updating player: $updatedPlayer")
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }
            currentPlayerLiveData.value = updatedPlayer
        } catch (
            e: Exception, ) {
            throw e
        }
    }

    override suspend fun updatePlayerScore(value: Int) {
        updatePlayerScore(value, currentPlayerLiveData.value!!.userId)
    }

    override suspend fun updatePlayerScore(
        value: Int,
        ownerId: String,
    ) {
        if (currentGameLiveData.value == null) throw Exception("No game found")
        if (currentPlayerLiveData.value == null) throw Exception("No player found")

        try {
            val updatedPlayer = currentPlayerLiveData.value!!.copy(
                score = currentPlayerLiveData.value!!.score + value,
            )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }

            currentPlayerLiveData.postValue(updatedPlayer)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlayerPosition(pos: Int) {
        if (currentGameLiveData.value == null) throw Exception("No game found")
        if (currentPlayerLiveData.value == null) throw Exception("No player found")

        try {
            val updatedPlayer = currentPlayerLiveData.value!!.copy(
                cellPosition = pos,
            )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    and {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
                        eq("user_id", updatedPlayer.userId)
                    }
                }
            }

            currentPlayerLiveData.postValue(updatedPlayer)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGamePlayers(): List<GamePlayer> {
        if (currentGameLiveData.value == null) throw Exception("No game found")

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
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
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

            currentPlayersLiveData.postValue(gamePlayers)

            return gamePlayers
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun setNextTurn(nextTurnPlayer: String) {
        if (currentGameLiveData.value == null) throw Exception("No game found")

        try {
            val updatedGame = currentGameLiveData.value!!.copy(turn = nextTurnPlayer)
            supabase.from(GAME_TABLE).update(updatedGame) {
                filter {
                    and {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("id", currentGameLiveData.value!!.id)
                    }
                }
            }

            currentGameLiveData.postValue(updatedGame)
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
        if (currentGameLiveData.value == null) throw Exception("No game found")

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
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("lobby_created_at", currentGameLiveData.value!!.lobbyCreatedAt)
                        eq("game_id", currentGameLiveData.value!!.id)
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
            val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: throw Exception("No user found")

            val historyItems: List<GameHistory> = supabase.from("v_games_history").select{
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

    suspend fun gameOver() {
        try {
            val game = currentGameLiveData.value ?: return

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

    suspend fun saveUserProgress() {
        Log.d("testlbbd GameRepository", "Saving user progress... ${currentPlayersLiveData.value}")

        if (currentPlayerLiveData.value == null || currentPlayerLiveData.value?.user == null) throw Exception()

        try {
            val user = currentPlayerLiveData.value!!.user!!
            Log.d("test GameRepository", "User: $user")
            val updatedUser = user.copy(
                totalScore = user.totalScore + currentPlayerLiveData.value!!.score,
                totalGames = user.totalGames + 1,
                totalWins = if (currentPlayerLiveData.value!!.winner) user.totalWins + 1 else user.totalWins,
            )
            Log.d("test GameRepository", "User: ${user.totalScore} + ${currentPlayerLiveData.value!!.score}")
            Log.d("TEST GameRepository", "asdaw ${user.totalScore + currentPlayerLiveData.value!!.score}")
            Log.d("TEST GameRepository", "Saving user progress: $updatedUser")
            supabase.from(USERS_TABLE).update(updatedUser) {
                filter { eq("id", user.id) }
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error saving user progress: ${e.message}")
            throw e
        }
    }

    suspend fun clearGameData() {
        try {
            val game = currentGameLiveData.value ?: return
            val player = currentPlayerLiveData.value ?: return
            val winner = currentPlayersLiveData.value?.maxByOrNull { it.score } ?: return

            val updatedPlayer = supabase.from(GAME_PLAYERS_TABLE)
                .update(mapOf("winner" to (winner.userId == player.userId))) {
                    filter {
                        eq("lobby_id", game.lobbyId)
                        eq("game_id", game.id)
                        eq("user_id", player.userId)
                    }
                    select()
                }.decodeSingleOrNull<GamePlayer>()

            currentPlayerLiveData.value = updatedPlayer

            currentGameLiveData.postValue(null)
            currentPlayersLiveData.postValue(emptyList())
        } catch (e: Exception) {
            Log.e("GameRepository", "Error clearing game data: ${e.message}")
            throw e
        }
    }
}
