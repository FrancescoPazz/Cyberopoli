package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@Composable
fun DefaultAvatarIcon(size: androidx.compose.ui.unit.Dp) {
    Icon(
        imageVector = Icons.Filled.PersonOutline,
        contentDescription = stringResource(R.string.avatar),
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
            .padding(12.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}
