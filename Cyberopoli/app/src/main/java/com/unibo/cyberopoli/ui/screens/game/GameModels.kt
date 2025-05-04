package com.unibo.cyberopoli.ui.screens.game

import androidx.compose.runtime.State
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.LobbyMember


enum class CellType(val resource: Int?) {
    YOUTUBE(R.drawable.ic_youtube), WHATSAPP(R.drawable.ic_whatsapp), REDDIT(R.drawable.ic_reddit), TIKTOK(
        R.drawable.ic_tiktok
    ),
    CHANCE(R.drawable.ic_chance), HACKER(R.drawable.ic_hacker), COMMON(null)
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
    WAIT, ROLL_DICE, MOVE, CHANCE, HACKER, END_TURN
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
