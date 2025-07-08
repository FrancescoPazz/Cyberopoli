package com.unibo.cyberopoli.ui.screens.profile.view.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberopoliGradientCard
import com.unibo.cyberopoli.ui.components.UserAvatarInfo

@Composable
fun ProfileHeader(
    user: User,
    onEditProfileClick: () -> Unit,
) {
    CyberopoliGradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
        ),
        contentPadding = 16.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            UserAvatarInfo(
                user = user,
                showWelcomeMessage = false,
                textStyleHeadline = MaterialTheme.typography.titleLarge,
                textStyleBody = MaterialTheme.typography.bodyMedium,
            )

            Row(horizontalArrangement = Arrangement.Start) {
                ProfileButton(
                    text = stringResource(R.string.change_avatar),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.change_avatar),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    },
                    onClick = onEditProfileClick,
                )
            }
        }
    }
}
