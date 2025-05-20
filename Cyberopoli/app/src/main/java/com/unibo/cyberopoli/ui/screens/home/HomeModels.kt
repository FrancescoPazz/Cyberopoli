package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GameHistory

data class HomeParams(
    val user: State<User?>,
    val gameHistories: State<List<GameHistory>?>,
)
