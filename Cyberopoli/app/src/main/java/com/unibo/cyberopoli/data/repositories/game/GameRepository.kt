package com.unibo.cyberopoli.data.repositories.game

import android.util.Log
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GamePlayerRaw
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.UUID
import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainGameRepository

class GameRepository(
    private val supabase: SupabaseClient
) : DomainGameRepository {

    override suspend fun createGame(lobbyId: String, lobbyMembers: List<LobbyMember>): Game {
        val newGame = Game(
            lobbyId = lobbyId,
            id      = UUID.randomUUID().toString(),
            turn    = lobbyMembers[0].userId
        )
        return try {
            val created: Game = supabase
                .from("games")
                .insert(newGame) { select() }
                .decodeSingle()
            created
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "createGame: ${e.message}")
            Game(lobbyId = lobbyId, id = newGame.id, turn = lobbyMembers[0].userId)
        }
    }

    override suspend fun joinGame(game: Game, userId: String): GamePlayer? {
        return try {
            val toInsert = mapOf(
                "lobby_id" to game.lobbyId,
                "game_id"  to game.id,
                "user_id"  to userId,
                "score"    to 50
            )

            val raw: GamePlayerRaw = supabase
                .from("game_players")
                .insert(toInsert) {
                    select(Columns.raw("""
                  lobby_id,
                  game_id,
                  user_id,
                  score,
                  users (
                    id,
                    username,
                    name,
                    surname,
                    avatar_url
                  )
                """.trimIndent()))
                }
                .decodeSingle()

            GamePlayer(
                lobbyId = raw.lobbyId,
                gameId  = raw.gameId,
                userId  = raw.userId,
                score   = raw.score,
                user    = raw.user
            )
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "joinGame: ${e.message}")
            null
        }
    }


    override suspend fun getGamePlayers(matchId: String): List<GamePlayer> {
        return try {
            val raw: List<GamePlayerRaw> = supabase
                .from("game_players")
                .select(
                    Columns.raw("""
                        lobby_id,
                        game_id,
                        user_id,
                        score,
                        users (
                          id,
                          username,
                          name,
                          surname,
                          avatar_url
                        )
                    """.trimIndent())
                ).decodeList()
            raw.map { r ->
                val u = r.user
                GamePlayer(
                    lobbyId = r.lobbyId,
                    gameId  = r.gameId,
                    userId  = r.userId,
                    score   = r.score,
                    user    = u
                )
            }
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "getGamePlayers: ${e.message}")
            emptyList()
        }
    }

    override suspend fun setNextTurn(game: Game, nextTurn: String): Game? {
        return try {
            val updated: Game = supabase
                .from("games")
                .update(mapOf("turn" to nextTurn)) {
                    filter {
                        eq("lobby_id", game.lobbyId)
                        eq("id", game.id)
                    }
                }
                .decodeSingle()

            updated
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "setNextTurn: ${e.message}")
            null
        }
    }

    override suspend fun addGameEvent(event: GameEvent): GameEvent? {
        return try {
            val inserted: GameEvent = supabase
                .from("game_events")
                .insert(event) {
                    select(Columns.raw("""
                        *,
                        users(
                          id,
                          username,
                          name,
                          surname,
                          avatar_url
                        )
                    """.trimIndent()))
                }
                .decodeSingle()

            inserted
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "addGameEvent: ${e.message}")
            null
        }
    }

    override suspend fun getGameEvents(lobbyId: String, gameId: String): List<GameEvent> {
        return try {
            val raw: List<GameEvent> = supabase
                .from("game_events")
                .select(
                    Columns.raw("""
                        lobby_id,
                        game_id,
                        sender_user_id,
                        event_type,
                        created_at,
                        users(
                          id,
                          username,
                          name,
                          surname,
                          avatar_url
                        )
                    """.trimIndent())
                ) {
                    filter {
                        eq("lobby_id", lobbyId)
                        eq("game_id", gameId)
                    }
                }
                .decodeList()
            raw
        } catch (e: Exception) {
            Log.e("GameRepoImpl", "getGameEvents: ${e.message}")
            emptyList()
        }
    }
}
