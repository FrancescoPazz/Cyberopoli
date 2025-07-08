package com.unibo.cyberopoli.ui.screens.home.view.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@SuppressLint("DefaultLocale")
@Composable
fun MostUsedAppsChart(
    appsUsage: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
) {
    if (appsUsage.isEmpty()) {
        Text(
            stringResource(R.string.no_stats_available),
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }
    val maxHours = appsUsage.maxOf { it.second }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        appsUsage.forEach { (packageName, hours) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = String.format("%.1f h", hours),
                        style = MaterialTheme.typography.bodyMedium,
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
