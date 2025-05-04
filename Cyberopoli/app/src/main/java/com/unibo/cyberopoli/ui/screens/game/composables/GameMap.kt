package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.ui.screens.game.Cell

@Composable
fun GameMap(
    cells: List<Cell>, rows: Int, cols: Int, players: List<GamePlayer>
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
        items(cells.size) { idx ->
            val cell = cells[idx]
            val isBorder = idx in PERIMETER_INDICES
            GameCell(cell, isBorder, posMap[idx].orEmpty().take(4))
        }
    }
}
