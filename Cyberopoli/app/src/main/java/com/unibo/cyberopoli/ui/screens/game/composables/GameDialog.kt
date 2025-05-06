package com.unibo.cyberopoli.ui.screens.game.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun GameDialog(
    title: String,
    message: String,
    options: List<String> = emptyList(),
    onOptionSelected: (Int) -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Text(message,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                if (options.isNotEmpty()) {
                    options.forEachIndexed { idx, label ->
                        Button(
                            onClick = { onOptionSelected(idx) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(label)
                        }
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
