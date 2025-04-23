package com.unibo.cyberopoli.ui.contracts

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.UserData

data class ProfileParams(
    val user: State<UserData?>,
    val loadUserData: () -> Unit,
    val changeAvatar: () -> Unit,
)