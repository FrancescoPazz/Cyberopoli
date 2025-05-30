package com.unibo.cyberopoli.ui.screens.profile.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Share
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
    onShareClick: () -> Unit,
) {
    CyberopoliGradientCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        gradientColors =
            listOf(
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                ProfileButton(
                    text = stringResource(R.string.edit),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    },
                    onClick = onEditProfileClick,
                )
                ProfileButton(
                    text = stringResource(R.string.share),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    },
                    onClick = onShareClick,
                )
            }
        }
    }
}
