package com.unibo.cyberopoli.ui.contracts

import androidx.lifecycle.LiveData

data class AuthParams(
    val authState: LiveData<AuthState>,
    val login: (email: String, password: String) -> Unit,
    val signUp: (email: String, password: String, name: String, surname: String) -> Unit,
)

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}