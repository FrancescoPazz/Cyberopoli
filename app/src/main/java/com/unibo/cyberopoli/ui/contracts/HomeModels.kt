package com.unibo.cyberopoli.ui.contracts

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.UserData

data class HomeParams(
    val user: State<UserData?>,
    val loadUserData: () -> Unit,
)