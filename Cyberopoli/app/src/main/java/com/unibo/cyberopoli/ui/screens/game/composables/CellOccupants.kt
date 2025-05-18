package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GamePlayer

@Composable
fun CellOccupants(
    occupants: List<GamePlayer>
) {
    @Composable
    fun getPlayerColor(index: Int) = when (index) {
        0 -> MaterialTheme.colorScheme.primary
        1 -> MaterialTheme.colorScheme.secondary
        2 -> MaterialTheme.colorScheme.tertiary
        3 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(Modifier.fillMaxSize()) {
        occupants.forEachIndexed { index, player ->
            val alignment = when (index) {
                0 -> Alignment.TopStart
                1 -> Alignment.TopEnd
                2 -> Alignment.BottomStart
                3 -> Alignment.BottomEnd
                else -> Alignment.Center
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(alignment)
                    .padding(1.dp)
                    .background(getPlayerColor(index), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val avatarRes = when (player.user?.avatarUrl) {
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
                        .size(20.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}