package com.unibo.cyberopoli.ui.screens.profile.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun GameStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    CyberopoliCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentPadding = 12.dp,
        verticalArrangement = Arrangement.Center,
    ) {
        GameStatItemContent(title, value, icon, iconTint, iconBackgroundColor)
    }
}

@Composable
private fun GameStatItemContent(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackgroundColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
