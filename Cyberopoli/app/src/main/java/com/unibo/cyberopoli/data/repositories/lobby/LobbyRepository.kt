package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberRaw
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.unibo.cyberopoli.data.repositories.lobby.ILobbyRepository as DomainLobbyRepository

const val LOBBY_TABLE = "lobbies"
const val LOBBY_MEMBERS_TABLE = "lobby_members"

@OptIn(SupabaseExperimental::class)
class LobbyRepository(
    private val supabase: SupabaseClient
) : DomainLobbyRepository {
    val currentLobbyLiveData: MutableLiveData<Lobby?> = MutableLiveData()
    val currentMembersLiveData: MutableLiveData<List<LobbyMember>?> = MutableLiveData(emptyList())

    override suspend fun createOrGetLobby(lobbyId: String, host: User) {
        val lobby = Lobby(
            id = lobbyId, hostId = host.id, status = LobbyStatus.WAITING.value
        )
        try {
            val created: Lobby = supabase.from(LOBBY_TABLE).upsert(lobby) {
                select()
                onConflict = "id"
            }.decodeSingle<Lobby>()
            currentLobbyLiveData.value = created

            val lobbyMembersFlow: Flow<List<LobbyMember>> = supabase.from(LOBBY_MEMBERS_TABLE).selectAsFlow(LobbyMember::userId,
                filter = FilterOperation("lobby_id", FilterOperator.EQ, created.id),
            )
            kotlinx.coroutines.MainScope().launch {
                lobbyMembersFlow.collect {
                    for (member in it) {
                        Log.d("LobbyRepoImpl", "Lobby member: ${member.userId}")
                    }
                }
            }
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
            supabase.from(LOBBY_MEMBERS_TABLE).insert(data) { select() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun fetchMembers(lobbyId: String): List<LobbyMember> {
        try {
            val raw: List<LobbyMemberRaw> = supabase.from(LOBBY_MEMBERS_TABLE).select(
                Columns.raw("*, users(*)")
            ){
                filter {
                    eq("lobby_id", lobbyId)
                }
            }.decodeList<LobbyMemberRaw>()
            val members = raw.map { d ->
                LobbyMember(
                    lobbyId = d.lobbyId,
                    userId = d.userId,
                    isReady = d.isReady,
                    joinedAt = d.joinedAt,
                    user = d.user
                )
            }
            currentMembersLiveData.value = members
            return members
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun toggleReady(
        isReady: Boolean
    ): LobbyMember {
        val userId = supabase.auth.currentSessionOrNull()?.user?.id.toString()
        val lobbyId = currentLobbyLiveData.value?.id ?: throw IllegalStateException("Lobby not found")
        return try {
            supabase.from(LOBBY_MEMBERS_TABLE).update(mapOf("ready" to isReady)) {
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
            supabase.from(LOBBY_MEMBERS_TABLE).delete {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
            }
            if (isHost) {
                supabase.from(LOBBY_TABLE).delete { filter { eq("id", lobbyId) } }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "leaveLobby: ${e.message}")
        }
    }

    override suspend fun startGame(lobbyId: String) {
        try {
            supabase.from(LOBBY_TABLE).update(mapOf("status" to LobbyStatus.IN_PROGRESS.value)) {
                filter { eq("id", lobbyId) }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "startGame: ${e.message}")
        }
    }
}
