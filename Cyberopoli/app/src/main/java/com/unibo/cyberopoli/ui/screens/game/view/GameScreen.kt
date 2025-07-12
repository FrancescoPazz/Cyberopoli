package com.unibo.cyberopoli.ui.screens.game.view

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
import com.unibo.cyberopoli.ui.screens.game.view.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.view.composables.GameDialog
import com.unibo.cyberopoli.ui.screens.game.view.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.game.view.composables.LoadingQuestionDialog
import com.unibo.cyberopoli.ui.screens.game.view.composables.QuestionResultDialog
import com.unibo.cyberopoli.ui.screens.game.viewmodel.GameParams
import com.unibo.cyberopoli.ui.screens.loading.view.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController,
    gameParams: GameParams,
) {
    val game by gameParams.game
    val user by gameParams.user
    val player by gameParams.player
    val players by gameParams.players
    val dialogData by gameParams.dialogData
    val isLoadingQuestion by gameParams.isLoadingQuestion

    AppLifecycleTracker(
        context = AppLifecycleTrackerScreenContext.GAME,
        setInApp = gameParams.setInApp,
        user = user ?: player?.user!!
    ) {
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
        gameParams.leaveLobby(user!!)
    }

    GameStarterEffect(gameParams)

    BackHandler {
        gameParams.leaveLobby(user!!)
        navController.navigate(CyberopoliRoute.Home) {
            launchSingleTop = true
            restoreState = true
        }
    }

    if (game == null || players == null || player == null) {
        if (gameParams.gameOver.value) {
            gameParams.refreshUserData()
            val hasWon = player?.winner ?: false
            val dialogTitle =
                if (hasWon) stringResource(R.string.you_win) else stringResource(R.string.you_lose)
            val dialogMessage = if (hasWon) stringResource(R.string.congratulations_you_won)
            else stringResource(R.string.better_luck_next_time)
            GameDialog(title = dialogTitle,
                message = dialogMessage,
                options = listOf("OK"),
                onOptionSelected = {
                    navController.navigate(CyberopoliRoute.Profile) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    gameParams.resetGame()
                },
                onDismiss = { })
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
        val (title, message, options) = when (data) {
            is GameDialogData.ChanceQuestion -> Triple(stringResource(data.titleRes), stringResource(data.promptRes), data.optionsRes.map { stringResource(it) })
            is GameDialogData.HackerStatement -> Triple(stringResource(data.titleRes), stringResource(data.contentRes), listOf("OK"))
            is GameDialogData.BlockChoice -> Triple(
                stringResource(data.titleRes),
                "",
                data.players.map { it.user?.username ?: it.userId },
            )

            is GameDialogData.SubscribeChoice -> Triple(
                stringResource(data.titleRes),
                data.messageArgs?.let { args -> stringResource(data.messageRes, *args.toTypedArray()) }
                    ?: stringResource(data.messageRes),
                data.optionsRes.map { stringResource(it) },
            )

            is GameDialogData.MakeContentChoice -> Triple(
                stringResource(data.titleRes),
                data.messageArgs?.let { args -> stringResource(data.messageRes, *args.toTypedArray()) }
                    ?: stringResource(data.messageRes),
                data.optionsRes.map { stringResource(it) },
            )

            is GameDialogData.QuestionResult -> Triple(
                stringResource(data.titleRes),
                data.messageArgs?.let { args -> stringResource(data.messageRes, *args.toTypedArray()) }
                    ?: stringResource(data.messageRes),
                data.optionsRes.map { stringResource(it) },
            )

            is GameDialogData.Alert -> Triple(
                stringResource(data.titleRes),
                data.messageArgs?.let { args -> stringResource(data.messageRes, *args.toTypedArray()) }
                    ?: stringResource(data.messageRes),
                data.optionsRes?.map { stringResource(it) } ?: listOf("OK"),
            )
        }

        if (dialogData is GameDialogData.QuestionResult) {
            QuestionResultDialog(data = dialogData as GameDialogData.QuestionResult,
                onDismiss = { gameParams.onResultDismiss() })
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
