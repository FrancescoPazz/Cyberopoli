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
import com.unibo.cyberopoli.ui.screens.game.composables.GameContent
import com.unibo.cyberopoli.ui.screens.game.composables.GameLifecycleHandler
import com.unibo.cyberopoli.ui.screens.game.composables.GameStarterEffect
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    var hasStarted by remember { mutableStateOf(false) }

    var stepsToAnimate by remember { mutableStateOf(0) }
    val animatedPos = remember { mutableStateMapOf<String, Int>() }

    GameLifecycleHandler(gameParams, navController)

    GameStarterEffect(gameParams, hasStarted) { hasStarted = true }

    BackHandler {
        gameParams.leaveGame()
        navController.popBackStack()
    }

    fun perimeterPath(rows: Int, cols: Int): List<Int> {
        val path = mutableListOf<Int>()
        for (c in 0 until cols)        path += c
        for (r in 1 until rows - 1)    path += r * cols + (cols - 1)
        for (c in cols - 1 downTo 0)    path += (rows - 1) * cols + c
        for (r in rows - 2 downTo 1)    path += r * cols
        return path
    }

    LaunchedEffect(stepsToAnimate) {
        if (stepsToAnimate <= 0) return@LaunchedEffect

        val gm       = gameParams.game.value!!
        val players  = gameParams.players.value
        val me       = players.first { it.userId == gm.turn }
        val path     = perimeterPath(rows = 5, cols = 5)
        val startIdx = path.indexOf(me.cellPosition).coerceAtLeast(0)

        repeat(stepsToAnimate) { step ->
            val idx = path[(startIdx + step + 1) % path.size]
            animatedPos[me.userId] = idx
            delay(500L)
        }

        gameParams.movePlayer()

        stepsToAnimate = 0
    }

    val game by gameParams.game
    val players by gameParams.players
    val turnIdx by gameParams.currentTurnIndex

    val displayPlayers = players.map { p ->
        p.copy(cellPosition = animatedPos[p.userId] ?: p.cellPosition)
    }

    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        GameContent(
            navController = navController,
            gameParams = gameParams,
            currentPlayer = players[turnIdx],
            players = displayPlayers,
            onMoveAnimated = { stepsToAnimate = it },
        )
    }
}