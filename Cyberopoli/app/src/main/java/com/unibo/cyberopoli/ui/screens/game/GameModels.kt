package com.unibo.cyberopoli.ui.screens.game

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GameAction
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class GameParams(
    val game: State<Game?>,
    val endTurn: () -> Unit,
    val lobby: State<Lobby?>,
    val rollDice: () -> Unit,
    val leaveGame: () -> Unit,
    val diceRoll: State<Int?>,
    val movePlayer: () -> Unit,
    val onResultDismiss: () -> Unit,
    val currentTurnIndex: State<Int>,
    val cells: State<List<GameCell>?>,
    val startAnimation: State<Boolean>,
    val players: State<List<GamePlayer>>,
    val isLoadingQuestion: State<Boolean>,
    val members: State<List<LobbyMember>?>,
    val dialogData: State<GameDialogData?>,
    val gameAction: State<List<GameAction>?>,
    val updatePlayerPoints: (value: Int) -> Unit,
    val onDialogOptionSelected: (idx: Int) -> Unit,
    val animatedPositions: State<Map<String, Int>>,
    val startGame: (lobbyId: String, members: List<LobbyMember>) -> Unit,
)