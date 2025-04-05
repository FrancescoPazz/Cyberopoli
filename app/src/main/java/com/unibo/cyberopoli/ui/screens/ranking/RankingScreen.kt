package com.unibo.cyberopoli.ui.screens.ranking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.data.models.RankingUser
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.ranking.MyRankingPosition
import com.unibo.cyberopoli.ui.composables.ranking.RankingListSection
import com.unibo.cyberopoli.ui.composables.ranking.RankingTabs
import com.unibo.cyberopoli.ui.composables.ranking.Top3RankingSection

@Composable
fun RankingScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val myPosition = when (selectedTabIndex) {
        1 -> RankingUser(6, "Marco Rossi", 1200, -1, "https://via.placeholder.com/150") // Settimanale
        2 -> RankingUser(2, "Marco Rossi", 4000, +3, "https://via.placeholder.com/150") // Mensile
        3 -> RankingUser(1, "Marco Rossi", 5000, 0, "https://via.placeholder.com/150")  // Scuola
        else -> RankingUser(4, "Marco Rossi", 2450, +2, "https://via.placeholder.com/150") // Generale
    }

    val top3 = when (selectedTabIndex) {
        1 -> listOf(
            RankingUser(2, "Luca Neri", 1800, 0, "https://via.placeholder.com/150"),
            RankingUser(1, "Giulia Verde", 1900, 0, "https://via.placeholder.com/150"),
            RankingUser(3, "Paolo Blu", 1700, 0, "https://via.placeholder.com/150")
        )
        2 -> listOf(
            RankingUser(2, "Chiara Viola", 4100, 0, "https://via.placeholder.com/150"),
            RankingUser(1, "Marco Rossi", 5000, 0, "https://via.placeholder.com/150"),
            RankingUser(3, "Franco Gialli", 3900, 0, "https://via.placeholder.com/150")
        )
        3 -> listOf(
            RankingUser(2, "Alunni Rossi", 4700, 0, "https://via.placeholder.com/150"),
            RankingUser(1, "Marco Rossi", 5000, 0, "https://via.placeholder.com/150"),
            RankingUser(3, "Simone Scuola", 4500, 0, "https://via.placeholder.com/150")
        )
        else -> listOf(
            RankingUser(2, "Sofia Bianchi", 3200, 0, "https://via.placeholder.com/150"),
            RankingUser(1, "Alessandro Conti", 3850, 0, "https://via.placeholder.com/150"),
            RankingUser(3, "Laura Ferrari", 2950, 0, "https://via.placeholder.com/150")
        )
    }

    val others = when (selectedTabIndex) {
        1 -> listOf(
            RankingUser(4, "Mario Rosso", 1600, 0, "https://via.placeholder.com/150"),
            RankingUser(5, "Angela Verde", 1500, 0, "https://via.placeholder.com/150")
        )
        2 -> listOf(
            RankingUser(4, "Cristina Azzurra", 3800, 0, "https://via.placeholder.com/150"),
            RankingUser(5, "Francesco Viola", 3600, 0, "https://via.placeholder.com/150")
        )
        3 -> listOf(
            RankingUser(4, "Team Scuola", 4400, 0, "https://via.placeholder.com/150"),
            RankingUser(5, "Classe 3B", 4300, 0, "https://via.placeholder.com/150")
        )
        else -> listOf(
            RankingUser(5, "Giulia Romano", 2300, 0, "https://via.placeholder.com/150"),
            RankingUser(6, "Roberto Marino", 2200, 0, "https://via.placeholder.com/150"),
            RankingUser(7, "Andrea Costa", 2000, 0, "https://via.placeholder.com/150")
        )
    }

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
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    onFilterClick = {
                        // TODO:
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                MyRankingPosition(user = myPosition)
                Spacer(modifier = Modifier.height(16.dp))
                Top3RankingSection(users = top3)
                Spacer(modifier = Modifier.height(16.dp))
                RankingListSection(users = others)
            }
        }
    )
}
