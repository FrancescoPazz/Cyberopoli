package com.unibo.cyberopoli.ui.screens.home.view.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.ui.components.CyberopoliCard
import java.time.LocalDateTime
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun MatchHistoryCard(
    gameHistory: List<GameHistory>?,
    modifier: Modifier = Modifier,
) {
    var showAll by remember { mutableStateOf(false) }

    CyberopoliCard(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            color = MaterialTheme.colorScheme.tertiary,
            text = stringResource(R.string.latest_games),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (gameHistory.isNullOrEmpty()) {
            Text(
                text = stringResource(R.string.no_games_played),
                color = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val sortedGameHistory = gameHistory.sortedByDescending { match ->
                try {
                    LocalDateTime.parse(match.lobbyCreatedAt)
                } catch (e: Exception) {
                    LocalDateTime.MIN
                }
            }

            val displayedGames = if (showAll || sortedGameHistory.size <= 3) {
                sortedGameHistory
            } else {
                sortedGameHistory.take(3)
            }

            displayedGames.forEach { match ->
                MatchHistoryItem(match)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (sortedGameHistory.size > 3 && !showAll) {
                Button(
                    onClick = { showAll = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = stringResource(R.string.load_all))
                }
            }
        }
    }
}
