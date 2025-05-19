package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun GameActionButton(
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
