package com.unibo.cyberopoli.ui.screens.auth.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Text3D(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 48,
    fontWeight: FontWeight = FontWeight.Bold,
    textColor: Color = MaterialTheme.colorScheme.surface,
    shadowColor: Color = MaterialTheme.colorScheme.background,
    offsetX: Int = 3,
    offsetY: Int = 3
) {
    Box {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            color = shadowColor,
            modifier = modifier.offset(x = offsetX.dp, y = offsetY.dp)
        )
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            color = textColor,
            modifier = modifier
        )
    }
}