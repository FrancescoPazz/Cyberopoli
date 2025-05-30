package com.unibo.cyberopoli.ui.screens.settings.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.unibo.cyberopoli.R

@Composable
fun NotificationSection(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(R.string.notifications),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.enable_notifications),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                ),
        )
    }
}
