package com.unibo.cyberopoli.data.models.lobby

import com.unibo.cyberopoli.data.models.auth.User
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LobbyMemberRaw(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("user_id") val userId: String,
    @SerialName("ready") var isReady: Boolean = false,
    @SerialName("joined_at") val joinedAt: String = Clock.System.now().toString(),
    @SerialName("in_app") val inApp: Boolean = true,
    @SerialName("users") val user: User,
)

@Serializable
data class LobbyMember(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("user_id") val userId: String,
    @SerialName("ready") var isReady: Boolean = false,
    @SerialName("joined_at") val joinedAt: String = Clock.System.now().toString(),
    @SerialName("in_app") val inApp: Boolean = true,
    @Transient val user: User? = null,
)
