package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberRaw
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import com.unibo.cyberopoli.data.repositories.lobby.ILobbyRepository as DomainLobbyRepository

class LobbyRepository(
    private val supabase: SupabaseClient
) : DomainLobbyRepository {
    val currentLobbyLiveData: MutableLiveData<Lobby?> = MutableLiveData()

    override suspend fun createOrGetLobby(lobbyId: String, host: User) {
        val lobby = Lobby(
            id = lobbyId, hostId = host.id, status = "waiting"
        )
        try {
            val created: Lobby = supabase.from("lobbies").insert(lobby) {
                select()
            }.decodeSingle<Lobby>()
            currentLobbyLiveData.value = created
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun joinLobby(member: LobbyMember) {
        val data = LobbyMember(
            lobbyId = currentLobbyLiveData.value?.id ?: throw IllegalStateException("Lobby not found"),
            userId = member.userId,
            isReady = member.isReady,
            joinedAt = member.joinedAt,
            user = member.user
        )
        try {
            supabase.from("lobby_members").insert(data) { select() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun fetchMembers(lobbyId: String): List<LobbyMember> = try {
        val raw: List<LobbyMemberRaw> = supabase.from("lobby_members").select(
            Columns.raw("*, users(*)")
        ).decodeList<LobbyMemberRaw>()
        raw.map { d ->
            LobbyMember(
                lobbyId = d.lobbyId,
                userId = d.userId,
                isReady = d.isReady,
                joinedAt = d.joinedAt,
                user = d.user
            )
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun toggleReady(
        lobbyId: String, userId: String, isReady: Boolean
    ): LobbyMember {
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
