package com.unibo.cyberopoli.data.models.lobby

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Lobby(
    @SerialName("id") val id: String,

    @SerialName("host_id") val hostId: String,

    @SerialName("status") var status: String,

    @SerialName("created_at") val createdAt: String = Instant.now().toString(),
)