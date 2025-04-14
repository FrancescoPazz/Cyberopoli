package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.home.StatCard

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.score_chart), style = MaterialTheme.typography.titleMedium
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Chart Placeholder")
                }

                Text(
                    text = stringResource(R.string.account_statistics), style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        scoreTitle = stringResource(R.string.total_score),
                        scoreValue = "1200",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        scoreTitle = stringResource(R.string.games_played),
                        scoreValue = "23",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        scoreTitle = stringResource(R.string.best_score),
                        scoreValue = "300",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        scoreTitle = stringResource(R.string.average_score),
                        scoreValue = "52",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        })
}

