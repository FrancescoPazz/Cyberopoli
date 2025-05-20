package com.unibo.cyberopoli.ui.screens.ranking

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
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.ranking.composables.MyRankingPosition
import com.unibo.cyberopoli.ui.screens.ranking.composables.RankingListSection
import com.unibo.cyberopoli.ui.screens.ranking.composables.Top3RankingSection

@Composable
fun RankingScreen(
    navController: NavController, rankingParams: RankingParams
) {
    val currentUser = rankingParams.user.value
    val rankingData = rankingParams.rankingData.value

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.surface,
        content = { paddingValues ->
            if (rankingData == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                val top3 = rankingData.take(3)
                val others = rankingData.drop(3)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    currentUser?.let {
                        MyRankingPosition(user = it, myRank = rankingData.indexOf(it) + 1, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    }

                    if (top3.isNotEmpty()) {
                        Top3RankingSection(users = top3)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (others.isNotEmpty()) {
                        RankingListSection(
                            users = others,
                            currentUser = currentUser,
                            rankOffset = 3
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}
