package com.unibo.cyberopoli.ui.screens.game.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun GameCell(
    gameCell: GameCell,
    isBorder: Boolean,
    occupants: List<GamePlayer>,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(if (isBorder) Modifier.border(2.dp, Color.Gray) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        gameCell.type.resource?.let { resId ->
            Image(
                painter = painterResource(resId),
                contentDescription = gameCell.title,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent),
            )
        }
        CellOccupants(occupants)
    }
}
