// File: app/src/main/java/com/unibo/cyberopoli/data/repositories/lobby/LobbyRepositoryImpl.kt
package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData
import com.unibo.cyberopoli.domain.model.LobbyMember
import com.unibo.cyberopoli.domain.model.User
import com.unibo.cyberopoli.domain.repository.ILobbyRepository as DomainLobbyRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.Instant
import java.util.UUID

class LobbyRepository(
    private val supabase: SupabaseClient
) : DomainLobbyRepository {

    override suspend fun createOrGetLobby(lobbyId: String, host: User): String {
        val lobby = Lobby(
            lobbyId = UUID.nameUUIDFromBytes(lobbyId.toByteArray()).toString(),
            hostId = host.id,
            status = "waiting"
        )
        return try {
            val created: Lobby = supabase.from("lobbies").upsert(lobby) {
                select()
            }.decodeSingle()
            created.lobbyId ?: throw IllegalStateException("Lobby ID is null")
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "createOrGetLobby: ${e.message}")
            lobbyId
        }
    }

    override suspend fun joinLobby(lobbyId: String, member: LobbyMember) {
        val data = LobbyMemberData(
            lobbyId = lobbyId,
            userId = member.user.id,
            isReady = member.isReady,
            joinedAt = member.joinedAt.toString(),
            displayName = member.user.displayName
        )
        try {
            supabase.from("lobby_members").insert(data) { select() }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "joinLobby: ${e.message}")
        }
    }

    override suspend fun fetchMembers(lobbyId: String): List<LobbyMember> = try {
        val raw: List<LobbyMemberData> = supabase.from("lobby_members")
            .select { filter { eq("lobby_id", lobbyId) } }
            .decodeList()
        raw.map { d ->
            LobbyMember(
                user = User(
                    id = d.userId!!,
                    displayName = d.displayName!!,
                    isGuest = false
                ),
                isReady = d.isReady ?: false,
                joinedAt = Instant.parse(d.joinedAt!!)
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
            }.decodeSingle<LobbyMemberData>()
            fetchMembers(lobbyId).first { it.user.id == userId }
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
