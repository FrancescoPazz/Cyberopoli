package com.unibo.cyberopoli.ui.screens.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.compose.runtime.State
import com.unibo.cyberopoli.domain.model.MatchPlayer

@Serializable
data class Match(
    @SerialName("id") val id: String,

    @SerialName("lobby_id") val lobbyId: String,

    @SerialName("started_at") val startedAt: String,

    @SerialName("ended_at") val endedAt: String? = null,
)

enum class SocialMediaType {
    YOUTUBE, WHATSAPP, REDDIT, TIKTOK
}

enum class CellType {
    SOCIAL_MEDIA, QUESTION, QUIZ, GAME, VIDEO
}

data class Cell(
    val id: String,
    val type: CellType,
    val title: String,
    val description: String,
    val imageUrl: String,
    val points: Int
)

data class MatchParams(
    val match: State<Match?>,
    val players: State<List<MatchPlayer>>,
    val currentTurnIndex: State<Int>,
    val nextTurn: () -> Unit
)