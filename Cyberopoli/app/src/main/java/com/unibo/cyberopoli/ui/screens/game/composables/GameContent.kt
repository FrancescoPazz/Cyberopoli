package com.unibo.cyberopoli.ui.screens.game.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.BOARD_COLS
import com.unibo.cyberopoli.data.models.game.BOARD_ROWS
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.ui.components.GameBottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.ar.ARBox
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameContent(
    navController: NavHostController,
    gameParams: GameParams,
) {
    val player by gameParams.player
    val players by gameParams.players
    val isArMode = remember { mutableStateOf(false) }
    val lobby = gameParams.lobby.value

    LaunchedEffect(players) {
        Log.d("GameContent", "Players updated: ${players ?: 0}")
    }

    LaunchedEffect(lobby?.status) {
        Log.d("TESTEA GameContent", "Lobby status: ${lobby?.status}")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isArMode.value) {
                TopBar(navController, onBackPressed = {
                    gameParams.leaveLobby()
                    navController.navigateUp()
                    navController.navigateUp()
                })
            }
        },
        bottomBar = {
            GameBottomBar(
                actions = gameParams.gameAction.value!!,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->

        if (isArMode.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {

                ARBox(
                    players = players,
                    cells = gameParams.cells.value,
                )

                IconButton(
                    onClick = { isArMode.value = false },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close AR Mode",
                        tint = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    player?.let {
                        ScoreAndManageRow(
                            score = it.score,
                            onManageClick = { isArMode.value = false },
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                players?.let {
                    GameMap(
                        gameCells = gameParams.cells.value!!,
                        rows = BOARD_ROWS,
                        cols = BOARD_COLS,
                        borderPath = PERIMETER_PATH,
                        players = it,
                    )

                    PlayerLegend(players = it)
                }

                Spacer(Modifier.weight(1f))

                player?.let {
                    ScoreAndManageRow(
                        score = it.score,
                        onManageClick = { isArMode.value = true },
                    )
                }
            }

        }
    }

}
