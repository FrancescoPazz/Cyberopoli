package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun MostUsedAppsCard(
    appsUsage: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    CyberopoliCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.most_used_apps_week),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        MostUsedAppsChart(
            appsUsage = appsUsage,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
        )
    }
}