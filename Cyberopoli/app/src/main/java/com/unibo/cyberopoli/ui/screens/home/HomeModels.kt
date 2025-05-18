package com.unibo.cyberopoli.ui.screens.home

import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.User

data class HomeParams(
    val user: State<User?>,
)
