package com.unibo.cyberopoli.data.repositories.game

import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameEventType
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.game.GameState
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.UUID
import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainGameRepository

const val GAME_TABLE = "games"
const val GAME_EVENTS_TABLE = "game_events"
const val GAME_PLAYERS_TABLE = "game_players"

class GameRepository(
    private val supabase: SupabaseClient
) : DomainGameRepository {
    val currentGameLiveData: MutableLiveData<Game?> = MutableLiveData()
    val currentPlayerLiveData: MutableLiveData<GamePlayer?> = MutableLiveData()

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
