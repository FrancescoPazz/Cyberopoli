package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GamePlayer(
    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("game_id") val gameId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("score") var score: Int = 50,
)
