package com.unibo.cyberopoli.data.repositories.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.services.LLMService
import com.unibo.cyberopoli.util.UsageStatsHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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
    val currentGameLiveData: MutableLiveData<Game?> = MutableLiveData()
    val currentTurnLiveData: MutableLiveData<String?> = MutableLiveData()
    val currentPlayerLiveData: MutableLiveData<GamePlayer?> = MutableLiveData()
    val currentPlayersLiveData: MutableLiveData<List<GamePlayer>> = MutableLiveData(emptyList())

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
        val dataJson =
            """
            {
              "topApps": "$topApps",
              "totalUsageSec": $totalSec,
              "sessionCount": $sessionCount,
              "averageSessionSec": $avgSessionSec,
              "unlockCount": $unlockCount,
            }
            """.trimIndent()
        Log.d("TEST", "Data JSON: $dataJson")

        val currentLanguage =
            if (Locale.getDefault().language == "it") {
                "ITALIANO"
            } else {
                "INGLESE"
            }
        val systemPrompt =
            """
            Genera 5 domande di gioco (in $currentLanguage) basandoti su questi dati utente:
            $dataJson
            Cerca di diversificare il più possibile il contenuto delle domande.
            Devono sembrare gli imprevisti di Monopoli ma in chiave informatica.
            Ad esempio: 
            Hai usato per troppo tempo il telefono questa settimana, perdi 5 punti.
            
            Ovviamente devi fare tu le domande basandoti sui dati che ti ho dato.
            """.trimIndent()

        val raw =
            llmService.generate(
                model = "cyberopoli_model",
                prompt = systemPrompt,
                stream = false,
            )

        val cleaned = raw.trim().removePrefix("```json").removeSuffix("```").trim()

        val payloads: List<GameDialogData.HackerStatement> =
            try {
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
        lobbyId: String,
        lobbyMembers: List<LobbyMember>,
    ) {
        val newGame =
            Game(
                lobbyId = lobbyId,
                id = UUID.randomUUID().toString(),
                turn = lobbyMembers[0].userId,
            )
        try {
            val existGame =
                supabase.from(GAME_TABLE).select {
                    filter { eq("lobby_id", lobbyId) }
                }.decodeSingleOrNull<Game>()

            if (existGame == null) {
                val created =
                    supabase.from(GAME_TABLE).upsert(newGame) {
                        select()
                    }.decodeSingle<Game>()
                currentGameLiveData.value = created
            } else {
                currentGameLiveData.value = existGame
            }
            Log.d("TEST", "Current game: ${currentGameLiveData.value}")

            observeGame()
            observeGamePlayers()
            observeTurn()
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeGame() {
        val gameFlow: Flow<Game?> =
            supabase.from(GAME_TABLE).selectSingleValueAsFlow(Game::id) {
                eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                eq("id", currentGameLiveData.value!!.id)
            }
        MainScope().launch {
            gameFlow.collect { rawGame ->
                if (rawGame != null) {
                    Log.d("TEST GameRepository", "Observe Game updated: $rawGame")
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
                filter =
                    FilterOperation(
                        "game_id",
                        FilterOperator.EQ,
                        currentGameLiveData.value!!.id,
                    ),
            )
        MainScope().launch {
            var seenOnce = false
            gamePlayersFlow.filter { rawPlayers ->
                if (!seenOnce) {
                    seenOnce = rawPlayers.isNotEmpty()
                    return@filter true
                } else {
                    rawPlayers.isNotEmpty()
                }
            }.collect { rawPlayers ->
                Log.d("TEST GameRepository", "Observe Game Players updated: $rawPlayers")
                val players =
                    rawPlayers.map { r ->
                        GamePlayer(
                            lobbyId = r.lobbyId,
                            gameId = r.gameId,
                            userId = r.userId,
                            score = r.score,
                            cellPosition = r.cellPosition,
                            round = r.round,
                            winner = r.winner,
                            user = r.user,
                        )
                    }
                currentPlayersLiveData.postValue(players)
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeTurn() {
        Log.d(
            "GameRepository",
            "observeTurn called. Current game ID: ${currentGameLiveData.value?.id}, Lobby ID: ${currentGameLiveData.value?.lobbyId}",
        )
        if (currentGameLiveData.value?.id == null || currentGameLiveData.value?.lobbyId == null) {
            Log.e("GameRepository", "Cannot observe turn, game ID or lobby ID is null.")
            return
        }

        val turnFlow: Flow<Game> =
            supabase.from(GAME_TABLE).selectSingleValueAsFlow(Game::turn) {
                eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                eq("id", currentGameLiveData.value!!.id)
            }
        MainScope().launch {
            Log.d(
                "GameRepository",
                "Starting to collect turnFlow for game ${currentGameLiveData.value!!.id} in observeTurn",
            )
            turnFlow.collect { game ->
                Log.d(
                    "TEST GameRepositoy",
                    "Observe Turn changed in repo: ${game.turn} for game ${game.id}",
                )
                currentTurnLiveData.postValue(game.turn)
            }
        }
    }

    override suspend fun joinGame(): GamePlayer? {
        if (currentGameLiveData.value == null) return null
        val session = supabase.auth.currentSessionOrNull() ?: return null

        val userId = session.user?.id
        try {
            val existingPlayer =
                supabase.from(GAME_PLAYERS_TABLE).select {
                    filter {
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("game_id", currentGameLiveData.value!!.id)
                        eq("user_id", userId!!)
                    }
                }.decodeSingleOrNull<GamePlayer>()

            if (existingPlayer != null) {
                val player =
                    GamePlayer(
                        lobbyId = existingPlayer.lobbyId,
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

            val toInsert =
                GamePlayer(
                    lobbyId = currentGameLiveData.value!!.lobbyId,
                    gameId = currentGameLiveData.value!!.id,
                    userId = userId!!,
                    score = 50,
                    cellPosition = 8,
                    round = 1,
                    winner = false,
                )
            val raw: GamePlayerRaw =
                supabase.from(GAME_PLAYERS_TABLE).insert(toInsert) {
                    select(
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
                    )
                }.decodeSingle()
            val created =
                GamePlayer(
                    lobbyId = raw.lobbyId,
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
            val updatedPlayer =
                currentPlayerLiveData.value!!.copy(
                    round = currentPlayerLiveData.value!!.round + 1,
                )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                    eq("game_id", currentGameLiveData.value!!.id)
                    eq("user_id", updatedPlayer.userId)
                }
            }
            currentPlayerLiveData.postValue(updatedPlayer)
        } catch (
            e: Exception,
        ) {
            throw e
        }
    }

    override suspend fun updatePlayerPoints(value: Int) {
        updatePlayerPoints(value, currentPlayerLiveData.value!!.userId)
    }

    override suspend fun updatePlayerPoints(
        value: Int,
        ownerId: String,
    ) {
        if (currentGameLiveData.value == null) throw Exception("No game found")
        if (currentPlayerLiveData.value == null) throw Exception("No player found")

        try {
            val updatedPlayer =
                currentPlayerLiveData.value!!.copy(
                    score = currentPlayerLiveData.value!!.score + value,
                )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                    eq("game_id", currentGameLiveData.value!!.id)
                    eq("user_id", updatedPlayer.userId)
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
            val updatedPlayer =
                currentPlayerLiveData.value!!.copy(
                    cellPosition = pos,
                )
            supabase.from(GAME_PLAYERS_TABLE).update(updatedPlayer) {
                filter {
                    eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                    eq("game_id", currentGameLiveData.value!!.id)
                    eq("user_id", updatedPlayer.userId)
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
            val raw: List<GamePlayerRaw> =
                supabase.from(GAME_PLAYERS_TABLE).select(
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
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("game_id", currentGameLiveData.value!!.id)
                    }
                }.decodeList()
            val gamePlayers =
                raw.map { r ->
                    val u = r.user
                    GamePlayer(
                        lobbyId = r.lobbyId,
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
                    eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                    eq("id", currentGameLiveData.value!!.id)
                }
            }

            currentGameLiveData.postValue(updatedGame)
            currentTurnLiveData.postValue(nextTurnPlayer)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addGameEvent(event: GameEvent): GameEvent {
        try {
            val inserted: GameEvent =
                supabase.from(GAME_EVENTS_TABLE).insert(event) {
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
                    eq("lobby_id", event.lobbyId)
                    eq("game_id", event.gameId)
                    eq("sender_user_id", event.senderUserId)
                    eq("event_type", event.eventType)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGameEvents(): List<GameEvent> {
        if (currentGameLiveData.value == null) throw Exception("No game found")

        try {
            val raw: List<GameEvent> =
                supabase.from(GAME_EVENTS_TABLE).select(
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
                        eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                        eq("game_id", currentGameLiveData.value!!.id)
                    }
                }.decodeList()
            return raw
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun getGamesHistory(): List<GameHistory> {
        try {
            val historyItems: List<GameHistory> =
                supabase.postgrest.rpc(
                    function = "get_games_history",
                ).decodeList()
            return historyItems
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
