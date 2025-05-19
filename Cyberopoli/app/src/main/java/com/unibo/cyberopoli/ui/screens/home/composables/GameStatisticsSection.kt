package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User

@Composable
fun GameStatisticsSection(user: User) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.your_game_stats),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GameStatItem(
                title = stringResource(R.string.total_score),
                value = user.totalScore.toString(),
                icon = Icons.Filled.Functions,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            GameStatItem(
                title = stringResource(R.string.games_played),
                value = user.totalGames.toString(),
                icon = Icons.Filled.Stadium,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GameStatItem(
                title = stringResource(R.string.wins),
                value = user.totalWins.toString(),
                icon = Icons.Filled.EmojiEvents,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            GameStatItem(
                title = stringResource(R.string.win_rate),
                value = if (user.totalGames > 0) "${(user.totalWins.toDouble() / user.totalGames * 100).toInt()}%" else "N/A",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconTint = MaterialTheme.colorScheme.tertiary,
                iconBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
