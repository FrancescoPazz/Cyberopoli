package com.unibo.cyberopoli.ui.screens.game

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

enum class SocialMediaType {
    YOUTUBE, WHATSAPP, REDDIT, TIKTOK
}

enum class CellType {
    SOCIAL_MEDIA, CHANCE, HACKER, COMMON
}

data class Cell(
    val id: String,
    val type: CellType,
    val title: String,
    val description: String,
    val imageUrl: String,
    val points: Int
)

enum class Phase {
    WAIT,
    ROLL_DICE,
    MOVE,
    CHANCE,
    HACKER,
    END_TURN
}

data class GameParams(
    val lobbyId: String,
    val lobbyMembers: List<LobbyMember>,
    val game: State<Game?>,
    val players: State<List<GamePlayer>>,
    val currentTurnIndex: State<Int>,
    val phase: State<Phase>,
    val diceRoll: State<Int?>,
    val startGame: (String, List<LobbyMember>) -> Unit,
    val rollDice: () -> Unit,
    val movePlayer: () -> Unit,
    val performChance: () -> Unit,
    val performHacker: () -> Unit,
    val endTurn: () -> Unit,
    val leaveGame: () -> Unit
)
