package com.unibo.cyberopoli.ui.screens.home.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun MostUsedAppsChart(
    appsUsage: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (appsUsage.isEmpty()) {
        Text("Nessuna statistica disponibile", style = MaterialTheme.typography.bodyMedium)
        return
    }
    val maxHours = appsUsage.maxOf { it.second }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        appsUsage.forEach { (packageName, hours) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = packageName.substringAfterLast('.'),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = String.format("%.1f h", hours),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (hours / maxHours).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
            }
        }
    }
}
