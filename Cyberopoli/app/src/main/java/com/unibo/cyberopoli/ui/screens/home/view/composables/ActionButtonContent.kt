package com.unibo.cyberopoli.ui.screens.home.view.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ActionButtonContent(
    icon: ImageVector,
    text: String,
    color: Color? = null,
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize),
        tint = color ?: LocalContentColor.current,
    )
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text(
        text,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = color ?: LocalContentColor.current,
    )
}
