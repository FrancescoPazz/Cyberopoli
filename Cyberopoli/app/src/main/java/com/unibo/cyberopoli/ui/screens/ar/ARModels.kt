package com.unibo.cyberopoli.ui.screens.ar

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GamePlayer

data class ARParams(
    val players: State<List<GamePlayer>?>,
    val cells: State<List<GameCell>?>,
)