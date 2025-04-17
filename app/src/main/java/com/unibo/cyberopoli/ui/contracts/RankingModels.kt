package com.unibo.cyberopoli.ui.contracts

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.RankingUser

data class RankingParams(
    val rankingData: State<List<RankingUser>?>,
    val loadUserData: () -> Unit,
    val getMyRanking: () -> RankingUser?,
)