package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.GameBottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.game.composables.GameMap
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen

@Composable
fun GameScreen(
    navController: NavHostController, gameParams: GameParams
) {
    var hasStarted by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                Log.d("LobbyScreen", "ON_STOP: leaving lobby")
                gameParams.leaveGame()
                navController.navigate(CyberopoliRoute.Home)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(gameParams.lobbyId, gameParams.lobbyMembers) {
        if (!hasStarted && gameParams.lobbyId.isNotBlank() && gameParams.lobbyMembers.isNotEmpty()) {
            gameParams.startGame(gameParams.lobbyId, gameParams.lobbyMembers)
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

    if (game == null || players.isEmpty() || players.getOrNull(turnIdx) == null) {
        LoadingScreen()
    } else {
        val currentPlayer = players[turnIdx]

        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(navController) },
            bottomBar = {
                GameBottomBar(phase = gameParams.phase.value,
                    diceRoll = gameParams.diceRoll.value,
                    onRoll = { gameParams.rollDice() },
                    onMove = {
                        gameParams.movePlayer()
                    },
                    onChance = { gameParams.performChance() },
                    onHacker = { gameParams.performHacker() },
                    onEndTurn = { gameParams.endTurn() })
            }) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFF1E1E2F))
            ) {
                GameMap(rows = 5, cols = 5, players = players)

                Spacer(Modifier.weight(1f))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF27293D))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${stringResource(R.string.internet_points)}: ${currentPlayer.score}", color = Color.White)
                    Button(onClick = { /* TODO:  */ }) {
                        Text(stringResource(R.string.manage))
                    }
                }
            }
        }
    }
}
