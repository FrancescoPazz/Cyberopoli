package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameStarterEffect(
    gameParams: GameParams, hasStarted: Boolean, onStarted: () -> Unit
) {
    LaunchedEffect(gameParams.lobby) {
        if (!hasStarted && gameParams.lobby.value != null && gameParams.members.value != null) {
            gameParams.startGame(
                gameParams.lobby.value!!.id, gameParams.members.value!!
            )
            onStarted()
        }
    }
}