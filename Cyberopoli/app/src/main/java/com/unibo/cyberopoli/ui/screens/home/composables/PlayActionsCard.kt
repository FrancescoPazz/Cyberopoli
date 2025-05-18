package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.cyberopoli.R

@Composable
fun PlayActionsCard(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit,
    onJoinGameClick: () -> Unit,
    onHowToPlayClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.play_now_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            GameActionButton(
                text = stringResource(R.string.new_game_button),
                icon = Icons.Filled.PlayCircleFilled,
                onClick = onNewGameClick,
                isPrimary = true
            )
            GameActionButton(
                text = stringResource(R.string.join_game_button),
                icon = Icons.AutoMirrored.Filled.Input,
                onClick = onJoinGameClick
            )
        }
    }
}

@Composable
private fun GameActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    isOutlined: Boolean = false
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(52.dp)

    when {
        isPrimary -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp
            )
        ) {
            ActionButtonContent(icon, text)
        }

        isOutlined -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.medium,
            border = ButtonDefaults.outlinedButtonBorder(
            ),
        ) {
            ActionButtonContent(icon, text, color = MaterialTheme.colorScheme.primary)
        }

        else -> FilledTonalButton(
            onClick = onClick,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.medium,
        ) {
            ActionButtonContent(icon, text)
        }
    }
}

@Composable
private fun ActionButtonContent(icon: ImageVector, text: String, color: Color? = null) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize),
        tint = color ?: LocalContentColor.current
    )
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text(
        text,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = color ?: LocalContentColor.current
    )
}