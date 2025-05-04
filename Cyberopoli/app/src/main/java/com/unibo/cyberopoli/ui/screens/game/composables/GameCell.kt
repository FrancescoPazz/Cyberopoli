package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun GameCell(
    isBorder: Boolean, occupants: List<GamePlayer>
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(if (isBorder) Modifier.border(2.dp, Color.Gray) else Modifier)
            .background(Color.Transparent), contentAlignment = Alignment.Center
    ) {
        CellOccupantsLayer(occupants)
    }
}
