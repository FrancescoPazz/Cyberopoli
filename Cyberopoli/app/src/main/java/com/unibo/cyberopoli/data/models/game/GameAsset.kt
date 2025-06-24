package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameAsset(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("game_id") val gameId: String,
    @SerialName("cell_id") val cellId: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("placed_at_round") val placedAtRound: Int,
    @SerialName("expires_at_round") val expiresAtRound: Int,
)
