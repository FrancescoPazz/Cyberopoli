package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberRaw
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.data.repositories.game.GAME_PLAYERS_TABLE
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.unibo.cyberopoli.data.repositories.lobby.ILobbyRepository as DomainLobbyRepository

const val LOBBY_TABLE = "lobbies"
const val LOBBY_MEMBERS_TABLE = "lobby_members"

@OptIn(SupabaseExperimental::class)
class LobbyRepository(
    private val supabase: SupabaseClient,
) : DomainLobbyRepository {
    val currentLobbyLiveData: MutableLiveData<Lobby?> = MutableLiveData()
    val currentMembersLiveData: MutableLiveData<List<LobbyMember>?> = MutableLiveData(emptyList())

    private val userCache = mutableMapOf<String, User>()

    override suspend fun createOrGetLobby(
        lobbyId: String,
        host: User,
    ) {
        val lobby = Lobby(id = lobbyId, hostId = host.id, status = LobbyStatus.WAITING.value)
        try {
            var currentLobby =
                supabase.from(LOBBY_TABLE).select {
                    filter { eq("id", lobbyId) }
                }.decodeSingleOrNull<Lobby>()

            if (currentLobby == null) {
                currentLobby =
                    supabase.from(LOBBY_TABLE).insert(lobby) {
                        select()
                    }.decodeSingle<Lobby>()
            }
            currentLobbyLiveData.value = currentLobby

            observeLobby()
            observeLobbyMembers()
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeLobby() {
        val lobbyFlow: Flow<Lobby?> =
            supabase.from(LOBBY_TABLE).selectSingleValueAsFlow(Lobby::id) {
                eq("id", currentLobbyLiveData.value!!.id)
            }
        MainScope().launch {
            lobbyFlow.collect { rawLobby ->
                if (rawLobby != null) {
                    Log.d("TEST LobbyRepository", "observeLobby: $rawLobby")
                    currentLobbyLiveData.value = rawLobby
                } else {
                    currentLobbyLiveData.value = null
                }
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeLobbyMembers() {
        val lobbyMembersFlow: Flow<List<LobbyMember>> =
            supabase.from(LOBBY_MEMBERS_TABLE).selectAsFlow(
                primaryKey = LobbyMember::userId,
                filter = FilterOperation("lobby_id", FilterOperator.EQ, currentLobbyLiveData.value!!.id),
            )
        MainScope().launch {
            var lastValid: List<LobbyMember> = emptyList()

            lobbyMembersFlow.collect { rawMembers ->

                if (rawMembers.isNotEmpty()) {
                    lastValid = rawMembers
                }

                val allIds = lastValid.map { it.userId }.distinct()
                val missingIds = allIds.filterNot { userCache.containsKey(it) }
                if (missingIds.isNotEmpty()) {
                    Log.d("TEST LobbyRepository", "ObserveLobbyMembers Fetching missing users: $missingIds")
                    val fetchedUsers: List<User> =
                        supabase.from("users").select {
                            filter { isIn("id", missingIds) }
                        }.decodeList<User>()

                    fetchedUsers.forEach { user ->
                        userCache[user.id] = user
                    }
                }

                val members =
                    lastValid.map { raw ->
                        LobbyMember(
                            lobbyId = raw.lobbyId,
                            userId = raw.userId,
                            isReady = raw.isReady,
                            joinedAt = raw.joinedAt,
                            user = userCache[raw.userId]!!,
                        )
                    }
                currentMembersLiveData.value = members
            }
        }
    }

    override suspend fun joinLobby(member: LobbyMember) {
        try {
            supabase.from(LOBBY_MEMBERS_TABLE).insert(member) { select() }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun setInApp(inApp: Boolean) {
        val session = supabase.auth.currentSessionOrNull()
        if (session == null) {
            Log.w("LobbyRepository", "setInApp: Sessione utente non disponibile, operazione annullata")
            return
        }

        val userId = session.user?.id
        val lobbyId = currentLobbyLiveData.value?.id ?: return

        try {
            supabase.from(LOBBY_MEMBERS_TABLE).update(mapOf("in_app" to inApp)) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId!!)
                }
            }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "setInApp: ${e.message}")
        }
    }

    override suspend fun fetchMembers(): List<LobbyMember> {
        try {
            val lobbyId =
                currentLobbyLiveData.value?.id
                    ?: throw IllegalStateException("Lobby not found")
            val raw: List<LobbyMemberRaw> =
                supabase.from(LOBBY_MEMBERS_TABLE).select(
                    Columns.raw("*, users(*)"),
                ) {
                    filter {
                        eq("lobby_id", lobbyId)
                    }
                }.decodeList<LobbyMemberRaw>()
            val members =
                raw.map { d ->
                    LobbyMember(
                        lobbyId = d.lobbyId,
                        userId = d.userId,
                        isReady = d.isReady,
                        joinedAt = d.joinedAt,
                        user = d.user,
                    )
                }
            currentMembersLiveData.value = members
            return members
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun toggleReady(isReady: Boolean): LobbyMember {
        val userId = supabase.auth.currentSessionOrNull()?.user?.id.toString()
        val lobbyId =
            currentLobbyLiveData.value?.id ?: throw IllegalStateException("Lobby not found")
        return try {
            supabase.from(LOBBY_MEMBERS_TABLE).update(mapOf("ready" to isReady)) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("user_id", userId)
                }
                select()
            }.decodeSingle<LobbyMember>()
            fetchMembers().first { it.userId == userId }
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "toggleReady: ${e.message}")
            throw e
        }
    }

    override suspend fun leaveLobby(isHost: Boolean) {
        try {
            val userId = supabase.auth.currentSessionOrNull()?.user?.id.toString()
            val lobbyId =
                currentLobbyLiveData.value?.id
                    ?: throw IllegalStateException("Lobby not found")

            if (isHost) {
                val currentMembers = fetchMembers()
                val remainingMembers = currentMembers.filter { it.userId != userId }

                if (remainingMembers.isEmpty()) {
                    supabase.from(LOBBY_TABLE).delete { filter { eq("id", lobbyId) } }
                } else {
                    val newHostId = remainingMembers.first().userId
                    supabase.from(LOBBY_TABLE).update(mapOf("host_id" to newHostId)) {
                        filter { eq("id", lobbyId) }
                    }
                }
            }

            supabase.from(LOBBY_MEMBERS_TABLE).delete {
                filter {
                    eq("user_id", userId)
                }
            }

            supabase.from(GAME_PLAYERS_TABLE).delete {
                filter {
                    eq("user_id", userId)
                }
            }
            setInApp(false)
            currentLobbyLiveData.value = null
            currentMembersLiveData.value = emptyList()
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "leaveLobby: ${e.message}")
        }
    }
}
