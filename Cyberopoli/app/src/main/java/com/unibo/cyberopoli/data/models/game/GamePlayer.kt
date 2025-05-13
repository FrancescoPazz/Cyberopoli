package com.unibo.cyberopoli.data.models.game

import com.unibo.cyberopoli.data.models.auth.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GamePlayerRaw(
    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("game_id") val gameId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("score") val score: Int,

    @SerialName("cell_position") val cellPosition: Int,

    @SerialName("users") val user: User
)

@Serializable
data class GamePlayer(
    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("game_id") val gameId: String,

    @SerialName("user_id") val userId: String,

    @SerialName("score") var score: Int,

    @SerialName("cell_position") val cellPosition: Int,

    @Transient val user: User? = null
)
