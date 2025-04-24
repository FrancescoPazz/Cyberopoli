package com.unibo.cyberopoli.data.models.lobby

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    @SerialName("lobby_id")
    val lobbyId: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("user_name")
    val name: String? = null,

    @SerialName("ready")
    val isReady: Boolean? = false,

    @SerialName("joined_at")
    val joinedAt: String? = null,
)