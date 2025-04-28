package com.unibo.cyberopoli.data.repositories

import android.util.Log
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class LobbyRepository(
    private val supabase: SupabaseClient
) {

    suspend fun createOrGetLobby(lobbyId: String, hostId: String): Lobby? = try {
        val lobby = Lobby(
            lobbyId = lobbyId, hostId = hostId, status = "waiting"
        )

        supabase.from("lobbies").upsert(lobby) {
                select()
            }.decodeSingle<Lobby>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "createOrGetLobby: ${e.message}")
        null
    }

    suspend fun joinLobby(player: LobbyMemberData): LobbyMemberData? = try {
        supabase.from("lobby_members").insert(player) {
                select()
            }.decodeSingle<LobbyMemberData>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "joinLobby: ${e.message}")
        null
    }

    suspend fun fetchPlayers(lobbyId: String): List<LobbyMemberData> = try {
        supabase.from("lobby_members").select {
                filter { eq("lobby_id", lobbyId) }
            }.decodeList()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "fetchPlayers: ${e.message}")
        emptyList()
    }

    suspend fun fetchCurrentPlayer(lobbyId: String, userId: String): LobbyMemberData? = try {
        supabase.from("lobby_members").select {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
            }.decodeSingle()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "fetchCurrentPlayer: ${e.message}")
        null
    }

    suspend fun toggleReady(player: LobbyMemberData): LobbyMemberData? = try {
        val newReady = !(player.isReady ?: false)

        supabase.from("lobby_members")
            .update(mapOf("ready" to newReady)) {
                filter {
                    eq("lobby_id", player.lobbyId!!)
                    eq("user_id", player.userId!!)
                }
                select()
            }
            .decodeSingle<LobbyMemberData>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "toggleReady: ${e.message}")
        null
    }

    suspend fun leaveLobby(lobbyId: String, userId: String, isHost: Boolean) {
        try {
            Log.d("LobbyRepo", "leaveLobby: $lobbyId, $userId, $isHost")
            supabase.from("lobby_members").delete {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
            }
            if (isHost) {
                supabase.from("lobbies").delete {
                    filter { eq("id", lobbyId) }
                }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepo", "leaveLobby: ${e.message}")
        }
    }

    suspend fun startGame(lobbyId: String) {
        try {
            val lobby = supabase.from("lobbies").select {
                    filter { eq("id", lobbyId) }
                }.decodeSingle<Lobby>()
            val lobbyData = lobby.copy(
                status = "started"
            )

            supabase.from("lobbies").update(lobbyData) {
                    filter { eq("id", lobbyId) }
                }
        } catch (e: Exception) {
            Log.e("LobbyRepo", "startGame: ${e.message}")
        }
    }
}
