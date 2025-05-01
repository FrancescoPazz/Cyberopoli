package com.unibo.cyberopoli.data.models.lobby

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Lobby(
    @SerialName("id") val id: String? = null,

    @SerialName("host_id") val hostId: String? = null,

    @SerialName("status") val status: String? = "waiting",

    @SerialName("created_at") val createdAt: String = Instant.now().toString(),
)