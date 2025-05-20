package com.unibo.cyberopoli.ui.screens.profile

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GameHistory

data class ProfileParams(
    val user: State<User?>,
    val changeAvatar: () -> Unit,
    val updateUserInfo: (String?, String?, () -> Unit, (String) -> Unit) -> Unit,
    val updatePasswordWithOldPassword: (oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
)