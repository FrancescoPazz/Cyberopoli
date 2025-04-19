package com.unibo.cyberopoli.ui.screens.ranking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.ranking.MyRankingPosition
import com.unibo.cyberopoli.ui.composables.ranking.RankingListSection
import com.unibo.cyberopoli.ui.composables.ranking.RankingTabs
import com.unibo.cyberopoli.ui.composables.ranking.Top3RankingSection
import com.unibo.cyberopoli.ui.contracts.RankingParams

@Composable
fun RankingScreen(
    navController: NavController, rankingParams: RankingParams
) {
    LaunchedEffect(Unit) {
        rankingParams.loadUserData()
    }

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                RankingTabs(
                    selectedTabIndex = 0,
                    onTabSelected = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(8.dp))
                if (rankingParams.rankingData.value == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(text = "Loading...", modifier = Modifier.padding(16.dp))
                } else {
                    val top3 = rankingParams.rankingData.value!!.take(3)
                    val others = rankingParams.rankingData.value!!.drop(3)

                    val myPosition = rankingParams.getMyRanking()

                    MyRankingPosition(user = myPosition)
                    Spacer(modifier = Modifier.height(16.dp))
                    Top3RankingSection(users = top3)
                    Spacer(modifier = Modifier.height(16.dp))
                    RankingListSection(users = others)
                }
            }
        })
}
