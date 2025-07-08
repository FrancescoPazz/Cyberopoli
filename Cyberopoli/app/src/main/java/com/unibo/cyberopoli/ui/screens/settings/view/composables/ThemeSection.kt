package com.unibo.cyberopoli.ui.screens.settings.view.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.theme.Theme

@Composable
fun ThemeSection(
    currentTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
) {
    Text(
        text = stringResource(R.string.theme),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
    Theme.entries.forEach { theme ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .selectable(
                    selected = theme == currentTheme,
                    onClick = { onThemeSelected(theme) },
                    role = Role.RadioButton,
                )
                .padding(horizontal = 16.dp),
        ) {
            RadioButton(
                selected = theme == currentTheme,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                ),
            )
            Text(
                text = when (theme) {
                    Theme.Light -> stringResource(R.string.light)
                    Theme.Dark -> stringResource(R.string.dark)
                    Theme.System -> stringResource(R.string.system)
                },
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
