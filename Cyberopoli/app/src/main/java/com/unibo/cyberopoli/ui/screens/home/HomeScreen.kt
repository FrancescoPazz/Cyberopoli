package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.home.composables.GameStatItem
import com.unibo.cyberopoli.ui.screens.home.composables.PlayActionsCard
import com.unibo.cyberopoli.ui.screens.home.composables.PlayerWelcomeCard
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    navController: NavController, homeParams: HomeParams
) {
    val currentUserState = homeParams.user
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = currentUserState.value) {
        if (currentUserState.value != null) {
            isLoading = false
        } else {
            delay(500)
            isLoading = false
        }
    }

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (isLoading) {
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

                PlayerWelcomeCard(user = currentUserState.value)

                PlayActionsCard(onNewGameClick = { /* TODO: Naviga a Nuova Partita */ },
                    onJoinGameClick = { /* TODO: Naviga a Unisciti Partita */ },
                    onHowToPlayClick = { /* TODO: Naviga al Tutorial/Regole */ })

                currentUserState.value?.let { user ->
                    GameStatisticsSection(user = user)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GameStatisticsSection(user: User) {
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