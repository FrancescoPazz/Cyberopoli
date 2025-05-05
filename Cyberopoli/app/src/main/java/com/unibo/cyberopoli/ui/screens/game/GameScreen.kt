package com.unibo.cyberopoli.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameDialog
import com.unibo.cyberopoli.ui.screens.game.composables.GameLifecycleHandler
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    navController: NavHostController,
    gameParams: GameParams
) {
    var hasStarted by remember { mutableStateOf(false) }

    val game       by gameParams.game
    val players    by gameParams.players
    val turnIdx    by gameParams.currentTurnIndex
    val phase      by gameParams.phase
    val diceRoll   by gameParams.diceRoll
    val dialogData by gameParams.dialogData

    var stepsToAnimate by remember { mutableStateOf(0) }
    val animatedPos = remember { mutableStateMapOf<String, Int>() }

    GameLifecycleHandler(gameParams, navController)
    GameStarterEffect(gameParams, hasStarted) { hasStarted = true }

    BackHandler {
        gameParams.leaveGame()
        navController.popBackStack()
    }

    LaunchedEffect(stepsToAnimate) {
        if (stepsToAnimate <= 0) return@LaunchedEffect
        val path = PERIMETER_PATH
        val me   = players.first { it.userId == game!!.turn }
        val startIdx = path.indexOf(me.cellPosition).coerceAtLeast(0)

        repeat(stepsToAnimate) { step ->
            val idx = path[(startIdx + step + 1) % path.size]
            animatedPos[me.userId] = idx
            delay(500L)
        }

        gameParams.movePlayer()
        stepsToAnimate = 0
    }

    val displayPlayers = players.map { p ->
        p.copy(cellPosition = animatedPos[p.userId] ?: p.cellPosition)
    }

    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        GameContent(
            navController    = navController,
            gameParams       = gameParams,
            currentPlayer    = players[turnIdx],
            players          = displayPlayers,
            onMoveAnimated   = { stepsToAnimate = it }
        )
    }

    dialogData?.let { data ->
        val (title, question, options) = when (data) {
            is GameDialogData.Chance -> Triple("Chance!", data.question, data.options)
            is GameDialogData.Hacker -> Triple("Hacker!", data.question, data.options)
        }
        GameDialog(
            title            = title,
            question         = question,
            options          = options,
            onOptionSelected = { idx ->
                val playerId = players[turnIdx].userId
                gameParams.onDialogOptionSelected(idx, playerId)
            },
            onDismiss        = { gameParams.onDialogDismiss() }
        )
    }
}
