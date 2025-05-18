package com.unibo.cyberopoli.ui.screens.profile.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = value,
            fontWeight = FontWeight.Bold
        )
        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}