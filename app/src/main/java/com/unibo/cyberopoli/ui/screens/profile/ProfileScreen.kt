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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.MatchHistory
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.profile.MatchHistorySection
import com.unibo.cyberopoli.ui.composables.profile.ProfileChartSection
import com.unibo.cyberopoli.ui.composables.profile.ProfileHeader
import com.unibo.cyberopoli.ui.composables.profile.ProfileStatsSection

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    val userData by profileViewModel.user.observeAsState()

    val matchHistory = listOf(
        MatchHistory(
            date = "15 Feb 2024",
            title = "Torneo Settimanale",
            result = "Vittoria",
            points = "+25 punti"
        ),
        MatchHistory(
            date = "14 Feb 2024",
            title = "Partita Amichevole",
            result = "Sconfitta",
            points = "-10 punti"
        )
    )

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (userData == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    ProfileHeader(
                        userData = userData!!,
                        onEditProfileClick = { profileViewModel.changeAvatar() },
                        onShareClick = { /* TODO: implementa share profilo */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    userData!!.totalGames?.let {
                        userData!!.totalWins?.let { it1 ->
                            userData!!.totalMedals?.let { it2 ->
                                ProfileStatsSection(
                                    totalGames = it,
                                    totalWins = it1,
                                    totalMedals = it2
                                )
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
        }
    )
}
