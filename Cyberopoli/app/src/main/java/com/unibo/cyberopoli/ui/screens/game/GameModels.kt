package com.unibo.cyberopoli.ui.screens.game

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer

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

data class GameParams(
    val game: State<Game?>,
    val players: State<List<GamePlayer>>,
    val currentTurnIndex: State<Int>,
    val nextTurn: () -> Unit
)