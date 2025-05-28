package com.unibo.cyberopoli.ui.screens.home.composables

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

@Composable
fun MatchHistoryCard(
    gameHistory: List<GameHistory>?,
    modifier: Modifier = Modifier,
) {
    CyberopoliCard(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            color = MaterialTheme.colorScheme.tertiary,
            text = stringResource(R.string.latest_games),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (gameHistory.isNullOrEmpty()) {
            Text(
                text = stringResource(R.string.no_games_played),
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            gameHistory.forEach { match ->
                MatchHistoryItem(match)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}