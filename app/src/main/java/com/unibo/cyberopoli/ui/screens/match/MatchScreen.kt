package com.unibo.cyberopoli.ui.screens.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    navController: NavHostController,
    matchParams: MatchParams
) {
    val match by matchParams.match
    val players by matchParams.players
    val currentTurnIndex by matchParams.currentTurnIndex

    if (match == null || players.isEmpty()) {
        Text("Caricamento partita...")
    } else {
        val currentPlayer = players[currentTurnIndex]

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Cyberopoli - Turno di ${currentPlayer.user.displayName}") })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Giocatori nella partita:")

                players.forEach { player ->
                    Text("${player.user.displayName}: ${player.score} punti")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { matchParams.nextTurn() }) {
                    Text("Passa al prossimo turno")
                }
            }
        }
    }
}
