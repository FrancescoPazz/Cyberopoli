package com.unibo.cyberopoli.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.profile.composables.MatchHistorySection
import com.unibo.cyberopoli.ui.screens.profile.composables.ProfileHeader
import com.unibo.cyberopoli.ui.screens.profile.composables.ProfileStatsSection

@Composable
fun ProfileScreen(
    navController: NavHostController, profileParams: ProfileParams
) {
    val user = profileParams.user.value
    val gameHistories = profileParams.gameHistories.value

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = if (user == null) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = if (user == null) Arrangement.Center else Arrangement.spacedBy(0.dp)
            ) {
                if (user == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    ProfileHeader(user = user,
                        onEditProfileClick = { profileParams.changeAvatar() },
                        onShareClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileStatsSection(
                        totalGames = user.totalGames,
                        totalWins = user.totalWins,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MatchHistorySection(gameHistory = gameHistories)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}