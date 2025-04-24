package com.unibo.cyberopoli.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.PlayerData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.UUID

class LobbyRepository(
    private val supabase: SupabaseClient
) {
    val currentPlayerLiveData = MutableLiveData<PlayerData?>()
    val playersLiveData = MutableLiveData<List<PlayerData>?>()

    suspend fun fetchCurrentPlayer(lobbyId: String, userId: String) {
        try {
            val player = supabase.from("lobby_members").select {
                    filter {
                        eq("lobby_id", lobbyId)
                        eq("user_id", userId)
                    }
                }.decodeSingle<PlayerData>()
            currentPlayerLiveData.postValue(player)
        } catch (e: Exception) {
            Log.e("LobbyRepo", "fetchCurrentPlayer: ${e.message}")
            currentPlayerLiveData.postValue(null)
        }
    }

    suspend fun getLobby(lobbyId: String): Lobby? = try {
        supabase.from("lobbies").select {
                filter {
                    eq("id", lobbyId)
                }
            }.decodeSingle<Lobby>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "getLobby: ${e.message}")
        null
    }

    suspend fun createLobby(
        lobbyId: String,
        hostId: String,
    ): Lobby? = try {
        val lobby = Lobby(
            lobbyId = UUID.fromString(lobbyId).toString(),
            hostId = hostId,
        )

        val json = JsonObject(
            mapOf(
                "host_id" to JsonPrimitive(hostId),
                "status" to JsonPrimitive("waiting")
            )
        )
        supabase.from("lobbies").upsert(json).decodeSingle<Lobby>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "createLobby: ${e.message}")
        null
    }

    suspend fun joinLobby(player: PlayerData) {
        try {
            supabase.from("lobby_members").upsert(player)
        } catch (e: Exception) {
            Log.e("LobbyRepo", "joinLobby: ${e.message}")
        }
    }

    suspend fun getLobbyPlayers(lobbyId: String): List<PlayerData>? = try {
        val players = supabase.from("lobby_members").select {
                filter {
                    eq("lobby_id", lobbyId)
                }
            }.decodeList<PlayerData>()
        playersLiveData.postValue(players)
        players
    } catch (e: Exception) {
        Log.e("LobbyRepo", "getLobbyPlayers: ${e.message}")
        null
    }

    suspend fun toggleReady(playerId: String, isReady: Boolean) {
        try {
            supabase.from("players").update(mapOf("is_ready" to isReady)) {
                    filter {
                        eq("id", playerId)
                    }
                }
        } catch (e: Exception) {
            Log.e("LobbyRepo", "toggleReady: ${e.message}")
        }
    }

    suspend fun leaveLobby(playerId: String) {
        try {
            supabase.from("players").delete { filter { eq("id", playerId) } }
        } catch (e: Exception) {
            Log.e("LobbyRepo", "leaveLobby: ${e.message}")
        }
    }

    suspend fun startGame(lobbyId: String) {
        try {
            supabase.from("lobbies").update(mapOf("status" to "started")) {
                    filter {
                        eq("id", lobbyId)
                    }
                }
        } catch (e: Exception) {
            Log.e("LobbyRepo", "startGame: ${e.message}")
        }
    }
}
