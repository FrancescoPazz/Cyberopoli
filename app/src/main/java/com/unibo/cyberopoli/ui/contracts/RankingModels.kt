package com.unibo.cyberopoli.ui.contracts

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.UserData

data class RankingParams(
    val rankingData: State<List<UserData>?>,
    val loadUserData: () -> Unit,
    val getMyRanking: () -> UserData?,
)