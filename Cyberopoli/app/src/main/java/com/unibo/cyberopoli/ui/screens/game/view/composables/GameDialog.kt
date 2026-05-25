package com.unibo.cyberopoli.ui.screens.game.view.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GameDialog(
    title: String,
    message: String,
    options: List<String> = emptyList(),
    helpTitle: String? = null,
    helpMessage: String? = null,
    onOptionSelected: (Int) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val showHelp = remember { mutableStateOf(false) }

    if (showHelp.value && helpMessage != null) {
        AlertDialog(
            onDismissRequest = { showHelp.value = false },
            title = { helpTitle?.let { Text(it) } },
            text = { Text(helpMessage) },
            confirmButton = {
                TextButton(onClick = { showHelp.value = false }) {
                    Text("OK")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        title,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (helpMessage != null) {
                        IconButton(
                            onClick = { showHelp.value = true },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Help",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(24.dp))
                if (options.isNotEmpty()) {
                    options.forEachIndexed { idx, label ->
                        Button(
                            onClick = { onOptionSelected(idx) },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                        ) {
                            Text(label)
                        }
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
