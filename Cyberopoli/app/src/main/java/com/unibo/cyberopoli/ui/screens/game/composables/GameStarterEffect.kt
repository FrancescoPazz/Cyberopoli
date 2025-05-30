package com.unibo.cyberopoli.ui.screens.game.composables

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameStarterEffect(gameParams: GameParams) {
    var hasStarted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(gameParams.lobby) {
        Log.d("GameStarterEffect", "Lobby: ${gameParams.lobby.value}")
        if (!hasStarted && gameParams.lobby.value != null && gameParams.members.value != null) {
            Log.d("GameStarterEffect", "Starting game with lobby ID: ${gameParams.lobby.value!!.id}")
            gameParams.startGame(
                gameParams.lobby.value!!.id,
                gameParams.members.value!!,
            )
            hasStarted = true
        }
    }
}
