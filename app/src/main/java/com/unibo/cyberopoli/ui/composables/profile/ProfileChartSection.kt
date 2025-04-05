package com.unibo.cyberopoli.ui.composables.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@Composable
fun ProfileChartSection(recentStats: List<Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.recent_stats), fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val maxValue = recentStats.maxOrNull()?.toFloat() ?: 1f

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val spacing = size.width / (recentStats.size - 1)
                val points = recentStats.mapIndexed { index, value ->
                    Offset(
                        x = index * spacing, y = size.height - (value / maxValue) * size.height
                    )
                }

                for (i in 0 until points.size - 1) {
                    val start = points[i]
                    val end = points[i + 1]
                    drawLine(
                        color = Color.Blue, start = start, end = end, strokeWidth = 4f
                    )
                }

                points.forEach { point ->
                    drawCircle(
                        color = Color.Red, radius = 6f, center = point
                    )
                }

                drawRect(
                    color = Color.Gray,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
