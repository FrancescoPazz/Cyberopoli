package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.lobby.ILobbyRepository as DomainLobbyRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.util.UUID

class LobbyRepository(
    private val supabase: SupabaseClient
) : DomainLobbyRepository {

    override suspend fun createOrGetLobby(lobbyId: String, host: User): String {
        val lobby = Lobby(
            id = UUID.nameUUIDFromBytes(lobbyId.toByteArray()).toString(),
            hostId = host.id,
            status = "waiting"
        )
        return try {
            val created: Lobby = supabase.from("lobbies").upsert(lobby) {
                select()
            }.decodeSingle()
            created.id ?: throw IllegalStateException("Lobby ID is null")
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "createOrGetLobby: ${e.message}")
            lobbyId
        }
    }

    override suspend fun joinLobby(lobbyId: String, member: LobbyMember) {
        val data = LobbyMember(
            lobbyId = lobbyId,
            userId = member.userId,
            isReady = member.isReady,
            joinedAt = member.joinedAt
        )
        try {
            supabase.from("lobby_members").insert(data) { select() }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "joinLobby: ${e.message}")
        }
    }

    override suspend fun fetchMembers(lobbyId: String): List<LobbyMember> = try {
        val raw: List<LobbyMember> = supabase.from("lobby_members")
            .select { filter { eq("lobby_id", lobbyId) } }
            .decodeList()
        raw.map { d ->
            LobbyMember(
                lobbyId = d.lobbyId,
                userId = d.userId,
                isReady = d.isReady,
                joinedAt = d.joinedAt
            )
        }
    } catch (e: Exception) {
        Log.e("LobbyRepoImpl", "fetchMembers: ${e.message}")
        emptyList()
    }

    override suspend fun toggleReady(lobbyId: String, userId: String, isReady: Boolean): LobbyMember {
        return try {
            supabase.from("lobby_members").update(mapOf("ready" to isReady)) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
                select()
            }.decodeSingle<LobbyMember>()
            fetchMembers(lobbyId).first { it.userId == userId }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "toggleReady: ${e.message}")
            throw e
        }
    }

    override suspend fun leaveLobby(lobbyId: String, userId: String, isHost: Boolean) {
        try {
            supabase.from("lobby_members").delete {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
            }
            if (isHost) {
                supabase.from("lobbies").delete { filter { eq("id", lobbyId) } }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "leaveLobby: ${e.message}")
        }
    }

    override suspend fun startGame(lobbyId: String) {
        try {
            supabase.from("lobbies").update(mapOf("status" to "in_progress")) {
                filter { eq("id", lobbyId) }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "startGame: ${e.message}")
        }
    }
}
