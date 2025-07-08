package com.unibo.cyberopoli.ui.screens.game.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.unibo.cyberopoli.util.AvatarUtils
import com.unibo.cyberopoli.util.PlayerColorUtils

@Composable
fun CellOccupants(occupants: List<GamePlayer>) {
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
                    .background(PlayerColorUtils.getPlayerColor(player), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                val avatarRes = AvatarUtils.getAvatarResourceForPlayer(player)

                Image(
                    painter = painterResource(avatarRes),
                    contentDescription = stringResource(R.string.avatar),
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
