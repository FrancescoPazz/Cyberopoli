package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameStarterEffect(
    gameParams: GameParams, hasStarted: Boolean, onStarted: () -> Unit
) {
    LaunchedEffect(gameParams.lobbyId, gameParams.lobbyMembers) {
        if (!hasStarted && gameParams.lobbyId.isNotBlank() && gameParams.lobbyMembers.isNotEmpty()) {
            gameParams.startGame(
                gameParams.lobbyId, gameParams.lobbyMembers
            )
            onStarted()
        }
    }
}