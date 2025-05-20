package com.unibo.cyberopoli.data.models.game

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameHistory(
    @SerialName("created_at") val dateCreated: Instant,

    @SerialName("score") val score: Int,

    @SerialName("winner") val winner: Boolean,
)
