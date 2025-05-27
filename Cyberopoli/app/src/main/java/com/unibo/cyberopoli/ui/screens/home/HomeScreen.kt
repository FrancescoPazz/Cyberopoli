package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.home.composables.MostUsedAppsChart
import com.unibo.cyberopoli.ui.screens.home.composables.PlayActionsCard
import com.unibo.cyberopoli.ui.screens.home.composables.PlayerWelcomeCard
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.profile.composables.MatchHistorySection


@Composable
fun HomeScreen(
    navController: NavController, homeParams: HomeParams
) {
    val topApps by homeParams.topAppsUsage
    val currentUserState = homeParams.user
    val gameHistories = homeParams.gameHistories.value

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (currentUserState.value == null) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                PlayerWelcomeCard(user = currentUserState.value!!)

                PlayActionsCard(onNewGameClick = { navController.navigate(CyberopoliRoute.Scan) })

                topApps?.let {
                    Text(
                        text = stringResource(R.string.most_used_apps_week),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    MostUsedAppsChart(
                        appsUsage = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                MatchHistorySection(gameHistory = gameHistories)
            }
        }
    }
}
