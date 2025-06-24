package com.unibo.cyberopoli.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.ui.components.AppLifecycleTracker
import com.unibo.cyberopoli.ui.components.AppLifecycleTrackerScreenContext
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameDialog
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.game.composables.LoadingQuestionDialog
import com.unibo.cyberopoli.ui.screens.game.composables.QuestionResultDialog
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController,
    gameParams: GameParams,
) {
    val game by gameParams.game
    val player by gameParams.player
    val players by gameParams.players
    val dialogData by gameParams.dialogData
    val isLoadingQuestion by gameParams.isLoadingQuestion

    AppLifecycleTracker(
        context = AppLifecycleTrackerScreenContext.GAME,
        setInApp = gameParams.setInApp,
    ) {
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
        gameParams.leaveLobby()
    }

    GameStarterEffect(gameParams)

    BackHandler {
        gameParams.leaveLobby()
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
    }

    if (game == null || players == null || player == null) {
        if (gameParams.gameOver.value) {
            gameParams.refreshUserData()
            val hasWon = player?.winner ?: false
            val dialogTitle = if (hasWon) stringResource(R.string.you_win) else stringResource(R.string.you_lose)
            val dialogMessage = if (hasWon)
                stringResource(R.string.congratulations_you_won)
            else
                stringResource(R.string.better_luck_next_time)
            navController.navigate(CyberopoliRoute.Profile) {
                launchSingleTop = true
                restoreState = true
            }
            GameDialog(
                title = dialogTitle,
                message = dialogMessage,
                options = listOf("OK"),
                onOptionSelected = { _ ->

                },
                onDismiss = {
                }
            )
        } else {
            LoadingScreen()
        }
    } else {
        GameContent(
            navController = navController,
            gameParams = gameParams,
        )
    }

    if (isLoadingQuestion) {
        LoadingQuestionDialog()
    }

    dialogData?.let { data ->
        val (title, message, options) =
            when (data) {
                is GameDialogData.ChanceQuestion -> Triple(data.title, data.prompt, data.options)
                is GameDialogData.HackerStatement -> Triple(data.title, data.content, listOf("OK"))
                is GameDialogData.BlockChoice ->
                    Triple(
                        data.title,
                        "",
                        data.players.map { it.user?.username ?: it.userId },
                    )
                is GameDialogData.SubscribeChoice ->
                    Triple(
                        data.title,
                        data.message,
                        data.options,
                    )
                is GameDialogData.MakeContentChoice ->
                    Triple(
                        data.title,
                        data.message,
                        data.options,
                    )
                is GameDialogData.QuestionResult ->
                    Triple(
                        data.title,
                        data.message,
                        data.options,
                    )
                is GameDialogData.Alert ->
                    Triple(
                        data.title,
                        data.message,
                        data.options ?: listOf("OK"),
                    )
            }

        if (dialogData is GameDialogData.QuestionResult) {
            QuestionResultDialog(
                data = dialogData as GameDialogData.QuestionResult,
                onDismiss = { gameParams.onResultDismiss() }
            )
        } else {
            GameDialog(
                title = title,
                message = message,
                options = options,
                onOptionSelected = { index ->
                    gameParams.onDialogOptionSelected(index)
                },
                onDismiss = {
                    gameParams.onResultDismiss()
                },
            )
        }
    }
}
