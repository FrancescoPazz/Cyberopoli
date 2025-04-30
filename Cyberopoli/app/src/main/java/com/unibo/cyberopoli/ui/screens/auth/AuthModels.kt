package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import androidx.lifecycle.LiveData

data class AuthParams(
    val authState: LiveData<AuthState>,
    val login: (email: String, password: String) -> Unit,
    val loginGoogleUser: (context: Context) -> Unit,
    val signUp: (email: String, password: String, name: String, surname: String) -> Unit,
    val resetPassword: (email: String) -> Unit,
    val loginAnonymously: (String) -> Unit
)

sealed class AuthState {
    data object Authenticated : AuthState()
    data object AnonymousAuthenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed interface AuthResponse {
    data object Success : AuthResponse
    data class Failure(val message: String) : AuthResponse
}