package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH

@Composable
fun GameMap(
    gameCells: List<GameCell>,
    rows: Int,
    cols: Int,
    borderPath: List<Int>,
    players: List<GamePlayer>
) {
    val posMap = remember(players) {
        players.groupBy { it.cellPosition }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(cols),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(cols / rows.toFloat())
            .padding(12.dp)
            .background(color = Color(0xFFC1C1C1)),
        userScrollEnabled = false
    ) {
        items(gameCells.size) { idx ->
            val cell = gameCells[idx]
            val isBorder = idx in borderPath
            GameCell(
                gameCell = cell,
                isBorder = isBorder,
                occupants = posMap[idx].orEmpty().take(4)
            )
        }
    }
}

