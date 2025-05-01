package com.unibo.cyberopoli.data.models.lobby

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class LobbyMember(
    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("ready") var isReady: Boolean = false,

    @SerialName("joined_at") val joinedAt: String = Instant.now().toString(),
)