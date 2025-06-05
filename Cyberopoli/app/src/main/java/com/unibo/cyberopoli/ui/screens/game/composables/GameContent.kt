package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.BOARD_COLS
import com.unibo.cyberopoli.data.models.game.BOARD_ROWS
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.ui.components.GameBottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameContent(
    navController: NavHostController,
    gameParams: GameParams,
) {
    val player by gameParams.player
    val players by gameParams.players

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(navController, onBackPressed = {
                gameParams.leaveLobby()
                navController.navigateUp()
                navController.navigateUp()
            })
        },
        bottomBar = {
            GameBottomBar(
                actions = gameParams.gameAction.value!!,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
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
            }

            Spacer(Modifier.weight(1f))

            player?.let {
                ScoreAndManageRow(
                    score = it.score,
                    onManageClick = { navController.navigate(CyberopoliRoute.AugmentedReality) },
                )
            }
        }
    }
}
