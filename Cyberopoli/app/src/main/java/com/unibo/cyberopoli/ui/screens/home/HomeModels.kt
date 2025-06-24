package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GameHistory

data class HomeParams(
    val user: State<User?>,
    val topAppsUsage: SnapshotStateList<Pair<String, Double>>,
    val gameHistories: SnapshotStateList<GameHistory>,
)
