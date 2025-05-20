package com.unibo.cyberopoli.data.repositories.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.game.questions.ChanceQuestions
import com.unibo.cyberopoli.data.models.game.questions.HackerStatements
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.services.LLMService
import com.unibo.cyberopoli.util.UsageStatsHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.Json
import java.util.UUID
import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainGameRepository

const val GAME_TABLE = "games"
const val GAME_EVENTS_TABLE = "game_events"
const val GAME_PLAYERS_TABLE = "game_players"

class GameRepository(
    private val supabase: SupabaseClient,
    private val llmService: LLMService,
    private val usageStatsHelper: UsageStatsHelper
) : DomainGameRepository {
    val currentGameLiveData: MutableLiveData<Game?> = MutableLiveData()
    val currentPlayerLiveData: MutableLiveData<GamePlayer?> = MutableLiveData()
    val chanceQuestions = MutableLiveData<List<GameDialogData.ChanceQuestion>>(ChanceQuestions)
    val hackerStatements = MutableLiveData<List<GameDialogData.HackerStatement>>(HackerStatements)

    companion object {
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    suspend fun preloadQuestionsForUser() {
        val newQuestions = fetchQuestions()
        hackerStatements.value = hackerStatements.value?.plus(newQuestions) ?: hackerStatements.value
    }

    private suspend fun fetchQuestions(
    ): List<GameDialogData.HackerStatement> {
        val topApps = usageStatsHelper.getTopUsedApps()
            .joinToString("; ") { "${it.first}:${it.second} h" }
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

        val systemPrompt =
            """
                Genera 5 domande di gioco (in ITALIANO) basandoti su questi dati utente:
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
            stream = false
        )

        val cleaned = raw
            .trim()
            .removePrefix("```json").removeSuffix("```")
            .trim()

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
                points = it.points
            )
        }
    }

    override suspend fun createGame(lobbyId: String, lobbyMembers: List<LobbyMember>) {
        val newGame = Game(
            lobbyId = lobbyId, id = UUID.randomUUID().toString(), turn = lobbyMembers[0].userId
        )
        try {
            val created: Game =
                supabase.from(GAME_TABLE).insert(newGame) { select() }.decodeSingle()
            currentGameLiveData.value = created
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun joinGame(): GamePlayer {
        if (currentGameLiveData.value == null) throw Exception("No game found")
        val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("User not logged in")

        try {
            val toInsert = GamePlayer(
                lobbyId = currentGameLiveData.value!!.lobbyId,
                gameId = currentGameLiveData.value!!.id,
                userId = userId,
                score = 50,
                cellPosition = 8,
                round = 1,
                winner = false
            )
            val raw: GamePlayerRaw = supabase.from(GAME_PLAYERS_TABLE).insert(toInsert) {
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
                        """.trimIndent()
                    )
                )
            }.decodeSingle()
            val created = GamePlayer(
                lobbyId = raw.lobbyId,
                gameId = raw.gameId,
                userId = raw.userId,
                score = raw.score,
                cellPosition = raw.cellPosition,
                round = raw.round,
                winner = raw.winner,
                user = raw.user
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
                round = currentPlayerLiveData.value!!.round + 1
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
            e: Exception
        ) {
            throw e
        }
    }

    override suspend fun updatePlayerPoints(value: Int) {
        updatePlayerPoints(value, currentPlayerLiveData.value!!.userId)
    }

    override suspend fun updatePlayerPoints(value: Int, ownerId: String) {
        if (currentGameLiveData.value == null) throw Exception("No game found")
        if (currentPlayerLiveData.value == null) throw Exception("No player found")

        try {
            val updatedPlayer = currentPlayerLiveData.value!!.copy(
                score = currentPlayerLiveData.value!!.score + value
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
            val updatedPlayer = currentPlayerLiveData.value!!.copy(
                cellPosition = pos
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
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("lobby_id", currentGameLiveData.value!!.lobbyId)
                    eq("game_id", currentGameLiveData.value!!.id)
                }
            }.decodeList()
            return raw.map { r ->
                val u = r.user
                GamePlayer(
                    lobbyId = r.lobbyId,
                    gameId = r.gameId,
                    userId = r.userId,
                    score = r.score,
                    cellPosition = r.cellPosition,
                    round = r.round,
                    winner = r.winner,
                    user = u
                )
            }
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
                        """.trimIndent()
                    )
                )
            }.decodeSingle()
            return inserted
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
                    """.trimIndent()
                )
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
            val historyItems: List<GameHistory> = supabase.postgrest.rpc(
                function = "get_games_history"
            ).decodeList()
            return historyItems
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
