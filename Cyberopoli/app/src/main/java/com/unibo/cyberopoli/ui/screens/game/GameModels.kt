package com.unibo.cyberopoli.ui.screens.game

import android.content.Context
import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GameState
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class GameParams(
    val lobby: State<Lobby?>,
    val members: State<List<LobbyMember>?>,
    val game: State<Game?>,
    val players: State<List<GamePlayer>>,
    val currentTurnIndex: State<Int>,
    val gameState: State<GameState?>,
    val diceRoll: State<Int?>,
    val dialogData: State<GameDialogData?>,
    val startGame: (lobbyId: String, members: List<LobbyMember>) -> Unit,
    val rollDice: () -> Unit,
    val movePlayer: () -> Unit,
    val onDialogOptionSelected: (idx: Int) -> Unit,
    val onResultDismiss: () -> Unit,
    val leaveGame: () -> Unit,
    val endTurn: () -> Unit,
    val isLoadingQuestion: State<Boolean>,
)