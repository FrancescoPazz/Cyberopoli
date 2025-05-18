package com.unibo.cyberopoli.ui.screens.profile.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameHistory

@Composable
fun MatchHistorySection(gameHistory: List<GameHistory>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = stringResource(R.string.latest_games),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        gameHistory.forEach { match ->
            MatchHistoryItem(match)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}