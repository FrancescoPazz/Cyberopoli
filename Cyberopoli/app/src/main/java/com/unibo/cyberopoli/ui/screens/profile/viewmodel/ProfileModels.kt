package com.unibo.cyberopoli.ui.screens.profile.viewmodel

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User

data class ProfileParams(
    val user: State<User?>,
    val changeAvatar: () -> Unit,
    val updateUserInfo: (String?, String?, () -> Unit, (String) -> Unit) -> Unit,
    val updatePasswordWithOldPassword: (oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
)
