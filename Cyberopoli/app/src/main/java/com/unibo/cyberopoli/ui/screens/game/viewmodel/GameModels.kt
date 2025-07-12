package com.unibo.cyberopoli.ui.screens.game.viewmodel

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameAction
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

data class GameParams(
    val game: State<Game?>,
    val user: State<User?>,
    val endTurn: () -> Unit,
    val lobby: State<Lobby?>,
    val rollDice: () -> Unit,
    val resetGame: () -> Unit,
    val diceRoll: State<Int?>,
    val movePlayer: () -> Unit,
    val gameOver: State<Boolean>,
    val player: State<GamePlayer?>,
    val onResultDismiss: () -> Unit,
    val refreshUserData: () -> Unit,
    val currentTurnIndex: State<Int>,
    val cells: State<List<GameCell>?>,
    val startAnimation: State<Boolean>,
    val leaveLobby: (user: User) -> Unit,
    val players: State<List<GamePlayer>?>,
    val isLoadingQuestion: State<Boolean>,
    val members: State<List<LobbyMember>?>,
    val dialogData: State<GameDialogData?>,
    val gameAction: State<List<GameAction>?>,
    val updatePlayerScore: (value: Int) -> Unit,
    val onDialogOptionSelected: (idx: Int) -> Unit,
    val setInApp: (user: User, inApp: Boolean) -> Unit,
    val startGame: (passedLobby: Lobby, members: List<LobbyMember>) -> Unit,
)
