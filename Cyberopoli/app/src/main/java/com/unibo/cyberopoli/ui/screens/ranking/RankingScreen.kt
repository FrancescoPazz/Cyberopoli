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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.ranking.composables.MyRankingPosition
import com.unibo.cyberopoli.ui.screens.ranking.composables.RankingListSection
import com.unibo.cyberopoli.ui.screens.ranking.composables.Top3RankingSection

@Composable
fun RankingScreen(
    navController: NavController, rankingParams: RankingParams
) {
    val user = rankingParams.user.value
    val rankingData = rankingParams.rankingData.value

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (rankingData == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(text = "Loading...", modifier = Modifier.padding(16.dp))
                } else {
                    val top3 = rankingData.take(3)
                    val others = rankingData.drop(3)
                    MyRankingPosition(user)
                    Spacer(modifier = Modifier.height(16.dp))
                    Top3RankingSection(users = top3)
                    Spacer(modifier = Modifier.height(16.dp))
                    RankingListSection(users = others)
                }
            }
        }
    )
}
