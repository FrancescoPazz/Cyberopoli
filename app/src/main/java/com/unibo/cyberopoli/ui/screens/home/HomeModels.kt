package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.UserData

data class HomeParams(
    val user: State<UserData?>,
    val loadUserData: () -> Unit,
)