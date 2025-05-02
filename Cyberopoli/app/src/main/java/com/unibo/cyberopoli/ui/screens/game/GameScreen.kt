package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.activity.compose.BackHandler
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

    LaunchedEffect(gameParams.lobbyId, gameParams.lobbyMembers, gameParams.game) {
        Log.d("GameScreen", "LaunchedEffect: ${gameParams.game.value} ${gameParams.lobbyId}, ${gameParams.lobbyMembers}")
        if (!hasStarted
            && gameParams.lobbyId.isNotBlank()
            && gameParams.lobbyMembers.isNotEmpty()
        ) {
            gameParams.startGame(
                gameParams.lobbyId,
                gameParams.lobbyMembers
            )
            hasStarted = true
        }
    }

    BackHandler {
        gameParams.leaveGame()
        navController.popBackStack()
    }

    val game by gameParams.game
    val players by gameParams.players
    val turnIdx by gameParams.currentTurnIndex

    if (game == null || players.isEmpty()) {
        LoadingScreen()
    } else {
        val currentPlayer = players[turnIdx]
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(navController) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text("${stringResource(R.string.players_in_game)}:")
                Spacer(Modifier.height(8.dp))
                players.forEach { p ->
                    Text("${p.user?.username}: ${p.score} pt.")
                }
                Spacer(Modifier.height(32.dp))
                Button(onClick = gameParams.nextTurn) {
                    Text(stringResource(R.string.turn_pass))
                }
            }
        }
    }
}
