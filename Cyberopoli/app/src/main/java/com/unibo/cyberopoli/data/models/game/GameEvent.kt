package com.unibo.cyberopoli.data.models.game


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class GameEvent(

    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("game_id") val gameId: String,

    @SerialName("sender_user_id") val senderUserId: String,

    @SerialName("recipient_user_id") val recipientUserId: String?,

    @SerialName("event_type") val eventType: GameEventType,

    @SerialName("value") val value: Int? = null,

    @SerialName("created_at") val createdAt: String = Instant.now().toString(),
)
