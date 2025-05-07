package com.unibo.cyberopoli.ui.screens.profile

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User

data class ProfileParams(
    val user: State<User?>,
    val changeAvatar: () -> Unit,
)