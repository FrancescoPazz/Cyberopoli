package com.unibo.cyberopoli.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameDialog
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.game.composables.LoadingQuestionDialog
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    val game by gameParams.game
    val players by gameParams.players
    val dialogData by gameParams.dialogData
    val turnIdx by gameParams.currentTurnIndex
    val isLoadingQuestion by gameParams.isLoadingQuestion
    var hasStarted by remember { mutableStateOf(false) }

    val animatedPositions by gameParams.animatedPositions
    val displayPlayers = players.map { p ->
        p.copy(cellPosition = animatedPositions[p.userId] ?: p.cellPosition)
    }

    GameStarterEffect(gameParams, hasStarted) { hasStarted = true }
    BackHandler { gameParams.leaveGame(); navController.popBackStack() }

    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        GameContent(
            navController = navController,
            gameParams = gameParams,
            currentPlayer = players[turnIdx],
            players = displayPlayers
        )
    }

    if (isLoadingQuestion) {
        LoadingQuestionDialog()
    }

    dialogData?.let { data ->
        val (title, message, options) = when (data) {
            is GameDialogData.ChanceQuestion -> Triple(data.title, data.prompt, data.options)
            is GameDialogData.HackerQuestion -> Triple(data.title, data.content, listOf("OK"))
            is GameDialogData.BlockChoice -> Triple(
                data.title,
                "",
                data.players.map { it.user?.username ?: it.userId }
            )
            is GameDialogData.Alert -> Triple(
                data.title,
                data.message,
                data.options ?: listOf("OK")
            )
        }

        GameDialog(
            title = title,
            message = message,
            options = options,
            onOptionSelected = { index ->
                gameParams.onDialogOptionSelected(index)
            },
            onDismiss = {
                gameParams.onResultDismiss()
            }
        )
    }
}