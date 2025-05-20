package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.UserAvatarInfo

@Composable
fun PlayerWelcomeCard(user: User, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                )
            ).padding(16.dp)
        ) {
            UserAvatarInfo(user = user, showWelcomeMessage = true)
        }
    }
}
