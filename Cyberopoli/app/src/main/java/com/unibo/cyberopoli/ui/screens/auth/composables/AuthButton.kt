package com.unibo.cyberopoli.ui.screens.auth.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp),
        elevation =
            ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp,
            ),
        colors =
            ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.onTertiary,
            ),
        enabled = enabled,
    ) {
        Text(
            text = text,
            style =
                TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
        )
    }
}
