package com.unibo.cyberopoli.data.models.lobby

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LobbyMemberData(
    @SerialName("lobby_id") val lobbyId: String? = null,

    @SerialName("user_id") val userId: String? = null,

    @SerialName("ready") val isReady: Boolean? = false,

    @SerialName("joined_at") val joinedAt: String? = null,

    @SerialName("display_name") val displayName: String? = null
)