package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.home.composables.MatchHistoryCard
import com.unibo.cyberopoli.ui.screens.home.composables.MostUsedAppsCard
import com.unibo.cyberopoli.ui.screens.home.composables.PlayActionsCard
import com.unibo.cyberopoli.ui.screens.home.composables.PlayerWelcomeCard
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.util.openUsageAccessSettings

@Composable
fun HomeScreen(
    navController: NavController,
    homeParams: HomeParams,
) {
    val context = navController.context
    val topApps = homeParams.topAppsUsage
    val currentUserState = homeParams.user
    val gameHistories = homeParams.gameHistories

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        if (currentUserState.value == null) {
            LoadingScreen()
        } else {
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                PlayerWelcomeCard(user = currentUserState.value!!)

                PlayActionsCard(onNewGameClick = { navController.navigate(CyberopoliRoute.Scan) })

                MostUsedAppsCard(
                    appsUsage = topApps,
                    onRequestPermission = {
                        context.openUsageAccessSettings()
                    },
                )

                MatchHistoryCard(gameHistory = gameHistories)

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
