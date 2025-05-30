package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberopoliGradientCard
import com.unibo.cyberopoli.ui.components.UserAvatarInfo

@Composable
fun PlayerWelcomeCard(
    user: User,
    modifier: Modifier = Modifier,
) {
    CyberopoliGradientCard(modifier = modifier) {
        UserAvatarInfo(user = user, showWelcomeMessage = true)
    }
}
