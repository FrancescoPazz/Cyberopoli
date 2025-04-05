package com.unibo.cyberopoli.ui.composables.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.MatchHistory

@Composable
fun MatchHistorySection(matchHistory: List<MatchHistory>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Ultime Partite", fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        matchHistory.forEach { match ->
            MatchHistoryItem(match)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MatchHistoryItem(match: MatchHistory) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center
        ) {
            Text(text = match.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = match.date)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Risultato: ${match.result}")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = match.points)
            }
        }
    }
}
