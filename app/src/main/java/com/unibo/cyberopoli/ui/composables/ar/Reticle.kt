package com.unibo.cyberopoli.ui.composables.ar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Reticle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(30.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val lineLength = 10f
            val strokeWidth = 2f
            drawLine(
                color = Color.Red,
                start = Offset(cx - lineLength, cy),
                end = Offset(cx + lineLength, cy),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = Color.Red,
                start = Offset(cx, cy - lineLength),
                end = Offset(cx, cy + lineLength),
                strokeWidth = strokeWidth
            )
        }
    }
}
