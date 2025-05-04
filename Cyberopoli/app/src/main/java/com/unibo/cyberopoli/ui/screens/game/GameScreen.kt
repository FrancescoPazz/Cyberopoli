package com.unibo.cyberopoli.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameLifecycleHandler
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    var hasStarted by remember { mutableStateOf(false) }

    GameLifecycleHandler(gameParams, navController)

    GameStarterEffect(gameParams, hasStarted) { hasStarted = true }

    BackHandler {
        gameParams.leaveGame()
        navController.popBackStack()
    }

    val game by gameParams.game
    val players by gameParams.players
    val turnIdx by gameParams.currentTurnIndex

    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        GameContent(
            navController = navController,
            gameParams = gameParams,
            currentPlayer = players[turnIdx],
            players = players
        )
    }
}