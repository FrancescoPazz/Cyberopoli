package com.unibo.cyberopoli.data.repositories

import android.util.Log
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.PlayerData
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

    suspend fun joinLobby(player: PlayerData): PlayerData? = try {
        supabase.from("lobby_members").insert(player) {
                select()
            }.decodeSingle<PlayerData>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "joinLobby: ${e.message}")
        null
    }

    suspend fun fetchPlayers(lobbyId: String): List<PlayerData> = try {
        supabase.from("lobby_members").select {
                filter { eq("lobby_id", lobbyId) }
            }.decodeList()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "fetchPlayers: ${e.message}")
        emptyList()
    }

    suspend fun fetchCurrentPlayer(lobbyId: String, userId: String): PlayerData? = try {
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

    suspend fun toggleReady(player: PlayerData): PlayerData? = try {
        val playerData = player.copy(
            isReady = !player.isReady!!
        )
        supabase.from("lobby_members").upsert(playerData) {
                select()
            }.decodeSingle<PlayerData>()
    } catch (e: Exception) {
        Log.e("LobbyRepo", "toggleReady: ${e.message}")
        null
    }

    suspend fun leaveLobby(lobbyId: String, userId: String) {
        try {
            supabase.from("lobby_members").delete {
                    filter {
                        eq("lobby_id", lobbyId)
                        eq("user_id", userId)
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
