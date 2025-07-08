package com.unibo.cyberopoli.ui.screens.home.view.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MatchHistoryItem(match: GameHistory) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = "${stringResource(R.string.games)} - ${
                    try {
                        val dateTime = LocalDateTime.parse(match.lobbyCreatedAt)
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        dateTime.format(formatter)
                    } catch (e: Exception) {
                        match.lobbyCreatedAt
                    }
                }",
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = "${stringResource(R.string.internet_points)}: ",
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    color = MaterialTheme.colorScheme.secondary,
                    text = "${match.score}",
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = "${stringResource(R.string.result)}: ",
                    fontWeight = FontWeight.Medium,
                )
                val resultColor =
                    when (match.winner) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.error
                    }
                Text(
                    color = resultColor,
                    text = if (match.winner) stringResource(R.string.win) else stringResource(R.string.loss),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
