package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("id") val id: String,
    @SerialName("turn") var turn: String,
)
