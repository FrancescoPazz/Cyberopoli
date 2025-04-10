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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.ranking.MyRankingPosition
import com.unibo.cyberopoli.ui.composables.ranking.RankingListSection
import com.unibo.cyberopoli.ui.composables.ranking.RankingTabs
import com.unibo.cyberopoli.ui.composables.ranking.Top3RankingSection

@Composable
fun RankingScreen(
    navController: NavController,
    rankingViewModel: RankingViewModel
) {
    val rankingData by rankingViewModel.ranking.observeAsState()

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                RankingTabs(
                    selectedTabIndex = 0,
                    onTabSelected = { /* TODO: implementa selezione */ },
                    onFilterClick = { /* TODO: Implementa filtro */ }
                )

                Spacer(modifier = Modifier.height(8.dp))
                if (rankingData == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(text = "Loading...", modifier = Modifier.padding(16.dp))
                } else {
                    val top3 = rankingData!!.take(3)
                    val others = rankingData!!.drop(3)

                    val myPosition = rankingData!!.first()

                    MyRankingPosition(user = myPosition)
                    Spacer(modifier = Modifier.height(16.dp))
                    Top3RankingSection(users = top3)
                    Spacer(modifier = Modifier.height(16.dp))
                    RankingListSection(users = others)
                }
            }
        }
    )
}
