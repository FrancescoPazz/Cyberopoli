package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.createBoard
import com.unibo.cyberopoli.ui.components.GameBottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.game.GameParams

@Composable
fun GameContent(
    navController: NavHostController,
    gameParams: GameParams,
    currentPlayer: GamePlayer,
    players: List<GamePlayer>,
    onMoveAnimated: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { TopBar(navController) }, bottomBar = {
        GameBottomBar(phase = gameParams.phase.value,
            diceRoll = gameParams.diceRoll.value,
            onRoll = { gameParams.rollDice() },
            onMove = { onMoveAnimated(it) },
            onEndTurn = { gameParams.endTurn() })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF1E1E2F))
        ) {
            val cells = remember { createBoard() }

            GameMap(
                cells = cells, rows = 5, cols = 5, players = players
            )

            Spacer(Modifier.weight(1f))

            ScoreAndManageRow(score = currentPlayer.score,
                onManageClick = { /* TODO: manage player */ })
        }
    }
}