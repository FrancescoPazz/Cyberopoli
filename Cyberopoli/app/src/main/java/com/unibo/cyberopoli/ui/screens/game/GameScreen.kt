package com.unibo.cyberopoli.ui.screens.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    navController: NavHostController, gameParams: GameParams
) {
    val match by gameParams.game
    val players by gameParams.players
    val currentTurnIndex by gameParams.currentTurnIndex

    if (match == null || players.isEmpty()) {
        Text(stringResource(R.string.loading))
    } else {
        val currentPlayer = players[currentTurnIndex]

        Scaffold(topBar = {
            TopAppBar(title = { Text("${stringResource(R.string.turn_of)} ${currentPlayer.userId}") })
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("${stringResource(R.string.players_in_game)}:")

                players.forEach { player ->
                    Text("${player.userId}: ${player.score} pt.")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { gameParams.nextTurn() }) {
                    Text(stringResource(R.string.turn_pass))
                }
            }
        }
    }
}