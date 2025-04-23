package com.unibo.cyberopoli.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.models.profile.MatchHistory
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.profile.MatchHistorySection
import com.unibo.cyberopoli.ui.composables.profile.ProfileChartSection
import com.unibo.cyberopoli.ui.composables.profile.ProfileHeader
import com.unibo.cyberopoli.ui.composables.profile.ProfileStatsSection
import com.unibo.cyberopoli.ui.contracts.ProfileParams

@Composable
fun ProfileScreen(
    navController: NavHostController, profileParams: ProfileParams
) {

    LaunchedEffect(Unit) {
        profileParams.loadUserData()
    }

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

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (profileParams.user.value == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(
                        text = stringResource(R.string.loading), modifier = Modifier.padding(16.dp)
                    )
                } else {
                    ProfileHeader(userData = profileParams.user.value!!,
                        onEditProfileClick = { profileParams.changeAvatar() },
                        onShareClick = { /* TODO */ })
                    Spacer(modifier = Modifier.height(16.dp))
                    (profileParams.user.value as? UserData)?.let { user ->
                        user.totalGames?.let { totalGames ->
                            user.totalWins?.let { totalWins ->
                                user.totalMedals?.let { totalMedals ->
                                    ProfileStatsSection(
                                        totalGames = totalGames,
                                        totalWins = totalWins,
                                        totalMedals = totalMedals
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileChartSection(
                        recentStats = listOf(5, 3, 7, 6, 8, 2, 9)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MatchHistorySection(matchHistory = matchHistory)
                }
            }
        })
}
