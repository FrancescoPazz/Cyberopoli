package com.unibo.cyberopoli.data.models.lobby

import com.unibo.cyberopoli.data.models.auth.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.Instant

@Serializable
data class LobbyMemberRaw(

    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("ready") var isReady: Boolean = false,

    @SerialName("joined_at") val joinedAt: String = Instant.now().toString(),

    @SerialName("users") val user: User,
)

@Serializable
data class LobbyMember(
    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("ready") var isReady: Boolean = false,

    @SerialName("joined_at") val joinedAt: String = Instant.now().toString(),

    @Transient val user: User? = null,
)