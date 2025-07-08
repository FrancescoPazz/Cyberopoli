package com.unibo.cyberopoli.ui.screens.home.view.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun PlayActionsCard(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit = {},
    onJoinGameClick: () -> Unit = {},
) {
    CyberopoliCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.play_now_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        GameActionButton(
            text = stringResource(R.string.new_game_button),
            icon = Icons.Filled.PlayCircleFilled,
            onClick = onNewGameClick,
            isPrimary = true,
        )
    }
}
