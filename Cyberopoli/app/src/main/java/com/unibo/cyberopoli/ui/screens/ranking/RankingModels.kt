package com.unibo.cyberopoli.ui.screens.ranking

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User

data class RankingParams(
    val user: State<User?>,
    val rankingData: State<List<User>?>,
)