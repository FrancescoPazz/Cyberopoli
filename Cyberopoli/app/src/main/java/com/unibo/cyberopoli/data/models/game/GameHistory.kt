package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameHistory(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("game_id") val gameId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score") val score: Int,
    @SerialName("winner") val winner: Boolean,
)
