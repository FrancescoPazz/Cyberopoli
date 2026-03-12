package com.unibo.cyberopoli.ui.screens.game.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun GameMap(
    gameCells: List<GameCell>,
    rows: Int,
    cols: Int,
    borderPath: List<Int>,
    players: List<GamePlayer>,
) {
    val posMap =
        remember(players) {
            players.groupBy { it.cellPosition }
        }
    LazyVerticalGrid(
        columns = GridCells.Fixed(cols),
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(cols / rows.toFloat())
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
        userScrollEnabled = false,
    ) {
        items(gameCells.size) { idx ->
            GameCell(
                gameCell = gameCells[idx],
                isBorder = idx in borderPath,
                occupants = posMap[idx].orEmpty().take(4),
            )
        }
    }
}
