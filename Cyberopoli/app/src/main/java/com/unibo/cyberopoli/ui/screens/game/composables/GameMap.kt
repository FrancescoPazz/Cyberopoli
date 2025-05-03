package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun GameMap(
    rows: Int,
    cols: Int,
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
            .padding(12.dp),
        userScrollEnabled = false
    ) {
        items(rows * cols) { idx ->
            val row = idx / cols
            val col = idx % cols
            val isBorder = row == 0 || row == rows - 1 || col == 0 || col == cols - 1
            val occupants = posMap[idx].orEmpty().take(4) // massimo 4

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .then(if (isBorder)
                        Modifier.border(2.dp, Color.Gray)
                    else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.fillMaxWidth().fillMaxWidth()) {
                    occupants.forEachIndexed { i, p ->
                        val alignment = when(i) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopEnd
                            2 -> Alignment.BottomStart
                            3 -> Alignment.BottomEnd
                            else -> Alignment.Center
                        }
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(alignment)
                                .padding(2.dp)
                                .background(Color.Cyan, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarRes = when (p.user?.avatarUrl) {
                                "avatar_male_1" -> R.drawable.avatar_male_1
                                "avatar_male_2" -> R.drawable.avatar_male_2
                                "avatar_female_1" -> R.drawable.avatar_female_1
                                "avatar_female_2" -> R.drawable.avatar_female_2
                                else -> R.drawable.avatar_male_1
                            }
                            Image(
                                painter = painterResource(avatarRes),
                                contentDescription = stringResource(R.string.avatar),
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                        }
                    }
                }
            }
        }
    }
}
