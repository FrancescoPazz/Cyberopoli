package com.unibo.cyberopoli.ui.screens.home.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun MostUsedAppsCard(
    appsUsage: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    CyberopoliCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.most_used_apps_week),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (appsUsage.isEmpty()) {
            Column(Modifier.padding(16.dp)) {
                Text("Per mostrare le app più usate e avere un'esperienza di gioco personalizzata, concedi “Accesso utilizzo”")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRequestPermission) {
                    Text("Apri impostazioni")
                }
            }
        } else {
            MostUsedAppsChart(
                appsUsage = appsUsage,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
            )
        }

    }
}
