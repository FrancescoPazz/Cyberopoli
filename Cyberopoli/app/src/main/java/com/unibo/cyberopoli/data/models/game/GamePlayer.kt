package com.unibo.cyberopoli.data.models.game

import com.unibo.cyberopoli.data.models.auth.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GamePlayerRaw(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("game_id") val gameId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score") val score: Int,
    @SerialName("cell_position") val cellPosition: Int,
    @SerialName("round") val round: Int,
    @SerialName("winner") val winner: Boolean,
    @SerialName("users") val user: User,
)

@Serializable
data class GamePlayer(
    @SerialName("lobby_id") val lobbyId: String,
    @SerialName("lobby_created_at") val lobbyCreatedAt: String,
    @SerialName("game_id") val gameId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score") var score: Int,
    @SerialName("cell_position") val cellPosition: Int,
    @SerialName("round") val round: Int,
    @SerialName("winner") val winner: Boolean,
    @Transient val user: User? = null,
)
