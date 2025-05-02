package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    var hasStarted by remember { mutableStateOf(false) }

    LaunchedEffect(gameParams.game) {
        Log.d("GameScreen", "Game: ${gameParams.game}")
        if (!hasStarted) {
            gameParams.startGame()
            hasStarted = true
        }
        Log.d("GameScreen", "Game2: ${gameParams.game}")
    }

    val game by gameParams.game
    val players by gameParams.players
    val currentTurnIndex by gameParams.currentTurnIndex

    if (game == null || players.isEmpty()) {
        LoadingScreen()
    } else {
        val currentPlayer = players[currentTurnIndex]

        Scaffold(topBar = {
            TopBar(navController)
        }, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
            ) {
                Text("${stringResource(R.string.players_in_game)}:")
                Spacer(modifier = Modifier.height(8.dp))

                players.forEach { player ->
                    Text("${player.user?.username}: ${player.score} pt.")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { gameParams.nextTurn() }) {
                    Text(stringResource(R.string.turn_pass))
                }
            }
        })
    }

}