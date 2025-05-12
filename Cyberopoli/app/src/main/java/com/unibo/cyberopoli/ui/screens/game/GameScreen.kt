package com.unibo.cyberopoli.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameDialog
import com.unibo.cyberopoli.ui.screens.game.composables.GameLifecycleHandler
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.game.composables.LoadingQuestionDialog
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    val game by gameParams.game
    val players by gameParams.players
    val context = LocalContext.current
    val dialogData by gameParams.dialogData
    val turnIdx by gameParams.currentTurnIndex
    val isLoadingQuestion by gameParams.isLoadingQuestion
    var stepsToAnimate by remember { mutableIntStateOf(0) }
    var hasStarted by remember { mutableStateOf(false) }
    val animatedPositions = remember { mutableStateMapOf<String, Int>() }
    val displayPlayers = players.map { p ->
        p.copy(cellPosition = animatedPositions[p.userId] ?: p.cellPosition)
    }

    GameLifecycleHandler(gameParams, navController)
    GameStarterEffect(gameParams, hasStarted) { hasStarted = true }
    BackHandler { gameParams.leaveGame(); navController.popBackStack() }
    LaunchedEffect(stepsToAnimate, players, game) {
        if (stepsToAnimate > 0 && game != null) {
            val path = PERIMETER_PATH
            val current = players.first { it.userId == game!!.turn }
            val startIdx = path.indexOf(current.cellPosition).coerceAtLeast(0)

            repeat(stepsToAnimate) { i ->
                animatedPositions[current.userId] = path[(startIdx + i + 1) % path.size]
                delay(500L)
            }
            gameParams.movePlayer(context)
            stepsToAnimate = 0
        }
    }
    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        GameContent(navController = navController,
            gameParams = gameParams,
            currentPlayer = players[turnIdx],
            players = displayPlayers,
            onMoveAnimated = { stepsToAnimate = it })
    }
    if (isLoadingQuestion) {
        LoadingQuestionDialog()
    }
    dialogData?.let { data ->
        val (title, message, options) = when (data) {
            is GameDialogData.ChanceQuestion -> Triple(data.title, data.prompt, data.options)
            is GameDialogData.HackerQuestion -> Triple(data.title, data.content, listOf("OK"))
            is GameDialogData.Result -> Triple(data.title, data.message, listOf("OK"))
        }
        GameDialog(title = title, message = message, options = options, onOptionSelected = { idx ->
            if (data is GameDialogData.ChanceQuestion) {
                gameParams.onDialogOptionSelected(idx)
            } else {
                gameParams.onResultDismiss()
            }
        }, onDismiss = { gameParams.onResultDismiss() })
    }
}