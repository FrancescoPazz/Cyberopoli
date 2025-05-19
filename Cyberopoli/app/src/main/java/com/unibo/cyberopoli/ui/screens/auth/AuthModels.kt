package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import androidx.compose.runtime.State
import com.unibo.cyberopoli.data.models.auth.AuthState

data class AuthParams(
    val authState: State<AuthState?>,
    val login: (email: String, password: String) -> Unit,
    val loginGoogleUser: (context: Context) -> Unit,
    val signUp: (
        name: String?, surname: String?, username: String, email: String, password: String
    ) -> Unit,
    val resetPassword: (email: String) -> Unit,
    val loginAnonymously: (String) -> Unit
)

