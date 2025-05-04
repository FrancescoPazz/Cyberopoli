package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun GameMap(
    rows: Int, cols: Int, players: List<GamePlayer>
) {
    val posMap = remember(players) {
        players.groupBy { it.cellPosition }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(cols),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(cols / rows.toFloat())
            .padding(12.dp),
        userScrollEnabled = false
    ) {
        items(rows * cols) { idx ->
            val row = idx / cols
            val col = idx % cols
            val isBorder = row == 0 || row == rows - 1 || col == 0 || col == cols - 1
            val occupants = posMap[idx].orEmpty().take(4)

            GameCell(
                isBorder = isBorder, occupants = occupants
            )
        }
    }
}
