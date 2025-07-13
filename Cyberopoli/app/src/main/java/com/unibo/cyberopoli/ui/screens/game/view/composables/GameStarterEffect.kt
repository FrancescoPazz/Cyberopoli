package com.unibo.cyberopoli.ui.screens.game.view.composables

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.unibo.cyberopoli.ui.screens.game.viewmodel.GameParams

@Composable
fun GameStarterEffect(gameParams: GameParams) {
    var hasStarted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(gameParams.lobby) {
        Log.d("GameStarterEffect", "Lobby: ${gameParams.lobby.value}")
        if (!hasStarted && gameParams.lobby.value != null && gameParams.members.value != null) {
            gameParams.startGame(
                gameParams.lobby.value!!,
                gameParams.members.value!!,
            )
            hasStarted = true
        }
    }

    LaunchedEffect(gameParams.isActionInProgress) {
        Log.d("sadsadawa", "iasd starteder sActionInProgress: ${gameParams.isActionInProgress.value}")
    }
}
