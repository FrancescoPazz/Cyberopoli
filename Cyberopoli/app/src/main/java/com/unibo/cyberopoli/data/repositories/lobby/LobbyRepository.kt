package com.unibo.cyberopoli.data.repositories.lobby

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.realtime.selectAsFlow
import com.unibo.cyberopoli.data.models.lobby.Lobby
import io.github.jan.supabase.postgrest.query.Columns
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyResponse
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberRaw
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import com.unibo.cyberopoli.data.repositories.game.GAME_PLAYERS_TABLE
import com.unibo.cyberopoli.data.repositories.lobby.ILobbyRepository as DomainLobbyRepository

const val LOBBY_TABLE = "lobbies"
const val LOBBY_MEMBERS_TABLE = "lobby_members"

@OptIn(SupabaseExperimental::class)
class LobbyRepository(
    private val supabase: SupabaseClient,
) : DomainLobbyRepository {
    private val _lobbyStateFlow = MutableStateFlow<Lobby?>(null)
    override val currentLobby: StateFlow<Lobby?> = _lobbyStateFlow

    private val _membersStateFlow = MutableStateFlow<List<LobbyMember>>(emptyList())
    override val currentLobbyMembers: StateFlow<List<LobbyMember>> = _membersStateFlow

    private val userCache = mutableMapOf<String, User>()

    override suspend fun createOrGetLobby(
        lobbyId: String,
        host: User,
    ): LobbyResponse {
        val lobby = Lobby(id = lobbyId, hostId = host.id, status = LobbyStatus.WAITING.value)
        try {
            val lobbyAlreadyStarted = supabase.from(LOBBY_TABLE).select {
                filter {
                    and {
                        eq("id", lobbyId)
                        eq("status", LobbyStatus.IN_PROGRESS.value)
                    }
                }
            }.decodeSingleOrNull<Lobby>()

            if (lobbyAlreadyStarted != null) {
                Log.w("LobbyRepository", "createOrGetLobby: Lobby already started")
                return LobbyResponse.AlreadyStarted
            }

            var currentLobby = supabase.from(LOBBY_TABLE).select {
                filter {
                    and {
                        eq("id", lobbyId)
                        eq("status", LobbyStatus.WAITING.value)
                    }
                }
            }.decodeSingleOrNull<Lobby>()

            if (currentLobby == null) {
                currentLobby = supabase.from(LOBBY_TABLE).insert(lobby) {
                    select()
                }.decodeSingle<Lobby>()
            }
            _lobbyStateFlow.value = currentLobby

            _membersStateFlow.value = emptyList()

            observeLobby()
            observeLobbyMembers()

            return LobbyResponse.Success
        } catch (e: Exception) {
            throw e
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeLobby() {
        val lobbyFlow: Flow<Lobby?> =
            supabase.from(LOBBY_TABLE).selectSingleValueAsFlow(Lobby::id) {
                and {
                    eq("id", _lobbyStateFlow.value!!.id)
                    eq("created_at", _lobbyStateFlow.value!!.createdAt)
                }
            }
        MainScope().launch {
            lobbyFlow.collect { rawLobby ->
                _lobbyStateFlow.value = rawLobby
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeLobbyMembers() {
        val lobbyMembersFlow: Flow<List<LobbyMember>> =
            supabase.from(LOBBY_MEMBERS_TABLE).selectAsFlow(
                primaryKey = LobbyMember::userId,
                filter = FilterOperation(
                    "lobby_id", FilterOperator.EQ, _lobbyStateFlow.value!!.id
                ),
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
                    val fetchedUsers: List<User> = supabase.from("users").select {
                        filter { isIn("id", missingIds) }
                    }.decodeList<User>()

                    fetchedUsers.forEach { user ->
                        userCache[user.id] = user
                    }
                }

                val members = lastValid.map { raw ->
                    LobbyMember(
                        lobbyId = raw.lobbyId,
                        lobbyCreatedAt = raw.lobbyCreatedAt,
                        userId = raw.userId,
                        isReady = raw.isReady,
                        joinedAt = raw.joinedAt,
                        user = userCache[raw.userId]!!,
                    )
                }
                _membersStateFlow.value = members
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
            Log.w(
                "LobbyRepository",
                "setInApp: Sessione utente non disponibile, operazione annullata"
            )
            return
        }

        val userId = session.user?.id
        val lobbyId = _lobbyStateFlow.value?.id ?: return
        val lobbyCreatedAt = _lobbyStateFlow.value?.createdAt
            ?: throw IllegalStateException("Lobby created_at not found")

        try {
            supabase.from(LOBBY_MEMBERS_TABLE).update(mapOf("in_app" to inApp)) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("lobby_created_at", lobbyCreatedAt)
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
                _lobbyStateFlow.value?.id ?: throw IllegalStateException("Lobby not found")
            val currentLobbyCreatedAt = _lobbyStateFlow.value?.createdAt
                ?: throw IllegalStateException("Lobby created_at not found")

            val raw: List<LobbyMemberRaw> = supabase.from(LOBBY_MEMBERS_TABLE).select(
                Columns.raw("*, users(*)"),
            ) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("lobby_created_at", currentLobbyCreatedAt)
                }
            }.decodeList<LobbyMemberRaw>()
            val members = raw.map { d ->
                LobbyMember(
                    lobbyId = d.lobbyId,
                    lobbyCreatedAt = d.lobbyCreatedAt,
                    userId = d.userId,
                    isReady = d.isReady,
                    joinedAt = d.joinedAt,
                    user = d.user,
                )
            }
            _membersStateFlow.value = members.distinctBy { it.userId }
            return members
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun toggleReady(isReady: Boolean): LobbyMember {
        val userId = supabase.auth.currentSessionOrNull()?.user?.id.toString()
        val lobbyId =
            _lobbyStateFlow.value?.id ?: throw IllegalStateException("Lobby not found")
        val currentLobbyCreatedAt = _lobbyStateFlow.value?.createdAt
            ?: throw IllegalStateException("Lobby created_at not found")
        return try {
            supabase.from(LOBBY_MEMBERS_TABLE).update(mapOf("ready" to isReady)) {
                filter {
                    eq("lobby_id", lobbyId)
                    eq("lobby_created_at", currentLobbyCreatedAt)
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
                _lobbyStateFlow.value?.id ?: throw IllegalStateException("Lobby not found")
            val createdAt = _lobbyStateFlow.value?.createdAt
                ?: throw IllegalStateException("Lobby created_at not found")

            if (isHost) {
                val currentMembers = fetchMembers()
                val remainingMembers = currentMembers.filter { it.userId != userId }

                Log.d("LobbyRepoImpl", "leaveLobby: Remaining members: ${remainingMembers.size}")
                Log.d("Lobby", "leasdawadaveLobby: ${_lobbyStateFlow.value}")

                if (remainingMembers.isEmpty()) {
                    supabase.from(LOBBY_TABLE).delete {
                        filter {
                            and {
                                eq("id", lobbyId)
                                eq("created_at", createdAt)
                            }
                        }
                    }
                } else {
                    val newHostId = remainingMembers.first().userId
                    supabase.from(LOBBY_TABLE).update(mapOf("host_id" to newHostId)) {
                        filter {
                            and {
                                eq("id", lobbyId)
                                eq("created_at", createdAt)
                            }
                        }
                    }
                }
            }

            supabase.from(LOBBY_MEMBERS_TABLE).delete {
                filter {
                    and {
                        eq("lobby_id", lobbyId)
                        eq("lobby_created_at", createdAt)
                        eq("user_id", userId)
                    }
                }
            }

            supabase.from(GAME_PLAYERS_TABLE).delete {
                filter {
                    and {
                        eq("lobby_id", lobbyId)
                        eq("lobby_created_at", createdAt)
                        eq("user_id", userId)
                    }
                }
            }
            setInApp(false)
            _lobbyStateFlow.value = null
            _membersStateFlow.value = emptyList()
        } catch (e: Exception) {
            Log.e("LobbyRepoImpl", "leaveLobby: ${e.message}")
        }
    }
}
