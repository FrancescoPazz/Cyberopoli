package com.unibo.cyberopoli.data.repositories.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameEventType
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.game.QuestionPayload
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.services.LLMService
import com.unibo.cyberopoli.util.UsageStatsHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
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
    val questions = MutableLiveData<List<GameDialogData.Question>>()
    val chanceQuestions = MutableLiveData<List<GameDialogData.Question>>()
    val hackerQuestions = MutableLiveData<List<GameDialogData.Question>>()

    suspend fun preloadQuestionsForUser(totalPerType: Int = 10) {
        questions.value = fetchQuestions()
        val allQuestions = questions.value ?: emptyList()
        val evenQuestions = if (allQuestions.size % 2 == 0) allQuestions else allQuestions.dropLast(1)
        val halfSize = evenQuestions.size / 2
        chanceQuestions.value = evenQuestions.take(halfSize)
        hackerQuestions.value = evenQuestions.drop(halfSize)
    }

    companion object {
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    private suspend fun fetchQuestions(
    ): List<GameDialogData.Question> {
        val topApps = usageStatsHelper.getTopUsedApps()
            .joinToString("; ") { "${it.first}:${it.second / 1000}s" }
        val totalSec = usageStatsHelper.getTodayUsageTime() / 1000
        val sessionStats = usageStatsHelper.getSessionStats()
        val sessionCount = sessionStats.sessionCount
        val avgSessionSec = sessionStats.averageSessionDuration / 1000
        val unlockCount = sessionStats.unlockCount
        val dndEnabled = usageStatsHelper.isDoNotDisturbEnabled()
        val interruptionFilter = usageStatsHelper.getInterruptionFilter()
        val dataJson =
            """
                {
                  "topApps": "$topApps",
                  "totalUsageSec": $totalSec,
                  "sessionCount": $sessionCount,
                  "averageSessionSec": $avgSessionSec,
                  "unlockCount": $unlockCount,
                  "doNotDisturb": $dndEnabled,
                  "interruptionFilter": $interruptionFilter
                }
            """.trimIndent()
        Log.d("TEST", "Data JSON: $dataJson")

        val systemPrompt =
            """
                Genera 2 domande (in ITALIANO) di gioco per l'evento "CHANCE" e 2 domande per l'evento "HACKER" utilizzando questi dati utente:
                $dataJson
                Cerca di diversificare il più possibile il contenuto delle domande,
                proponendo anche quesiti di cultura generale legati alla cybersecurity.
                Escludi domande sulle applicazioni di sistema (es. com.sec.android.app.launcher).
                Output DEVE ESSERE un JSON valido ESATTAMENTE in questo formato:
                [
                  {
                    "title": "Titolo domanda",
                    "prompt": "Il testo della domanda",
                    "options": ["o1","o2","o3"],
                    "correctIndex": 0,
                    "eventType": "CHANCE"
                  },
                  {
                    "title": "Titolo domanda 2",
                    "prompt": "Il testo della domanda 2",
                    "options": ["o1","o2","o3"],
                    "correctIndex": 1,
                    "eventType": "HACKER"
                  }
                ]
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

        val payloads: List<QuestionPayload> = try {
            jsonParser.decodeFromString(cleaned)
        } catch (e: Exception) {
            Log.e("TEST", "Failed to decode questions JSON: ${e.message}")
            emptyList()
        }

        Log.d("TEST", "Decoded $payloads")

        return payloads.map {
            GameDialogData.Question(
                title = it.title,
                prompt = it.prompt,
                options = it.options,
                correctIndex = it.correctIndex
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
                score = 50
            )
            val raw: GamePlayerRaw = supabase.from(GAME_PLAYERS_TABLE).insert(toInsert) {
                select(
                    Columns.raw(
                        """
                        lobby_id,
                        game_id,
                        user_id,
                        score,
                        cell_position,
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
                user = raw.user
            )
            currentPlayerLiveData.value = created
            return created
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlayerPoints(value: Int) {
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
                    lobby_id,
                    game_id,
                    user_id,
                    score,
                    cell_position,
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
}
