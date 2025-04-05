package com.unibo.cyberopoli.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.MatchHistory
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.profile.MatchHistorySection
import com.unibo.cyberopoli.ui.composables.profile.ProfileChartSection
import com.unibo.cyberopoli.ui.composables.profile.ProfileHeader
import com.unibo.cyberopoli.ui.composables.profile.ProfileStatsSection

@Composable
fun ProfileScreen(navController: NavHostController) {
    val userData = UserData(
        avatarUrl = "https://via.placeholder.com/150",
        name = "Marco Rossi",
        role = "Giocatore Appassionato \uD83C\uDFAE",
        totalGames = 127,
        totalWins = 84,
        totalMedals = 12
    )

    val matchHistory = listOf(
        MatchHistory(
            date = "15 Feb 2024",
            title = "Torneo Settimanale",
            result = "Vittoria",
            points = "+25 punti"
        ), MatchHistory(
            date = "14 Feb 2024",
            title = "Partita Amichevole",
            result = "Sconfitta",
            points = "-10 punti"
        )
    )

    Scaffold(topBar = { TopBar(navController, title = stringResource(R.string.home)) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeader(userData = userData,
                    onEditProfileClick = { /* TODO */ },
                    onShareClick = { /* TODO */ })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileStatsSection(
                    totalGames = userData.totalGames,
                    totalWins = userData.totalWins,
                    totalMedals = userData.totalMedals
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileChartSection(
                    recentStats = listOf(5, 3, 7, 6, 8, 2, 9)
                )
                Spacer(modifier = Modifier.height(16.dp))
                MatchHistorySection(matchHistory = matchHistory)
            }
        })
}
