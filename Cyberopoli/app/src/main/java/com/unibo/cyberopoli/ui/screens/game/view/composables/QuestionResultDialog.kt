package com.unibo.cyberopoli.ui.screens.game.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.unibo.cyberopoli.data.models.game.GameDialogData

@Composable
fun QuestionResultDialog(
    data: GameDialogData.QuestionResult, onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnClickOutside = false, dismissOnBackPress = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    stringResource(data.titleRes),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    data.messageArgs?.let { args ->
                        stringResource(data.messageRes, *args.toTypedArray())
                    } ?: stringResource(data.messageRes),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(24.dp))

                data.optionsRes.forEachIndexed { idx, label ->
                    val isCorrect = idx == data.correctIndex
                    val isSelected = idx == data.selectedIndex
                    val backgroundColor = when {
                        isCorrect -> Color.Green.copy(alpha = 0.2f)
                        isSelected -> Color.Red.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                    val borderColor = when {
                        isCorrect -> Color.Green
                        isSelected -> Color.Red
                        else -> Color.Transparent
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = stringResource(label), modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
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