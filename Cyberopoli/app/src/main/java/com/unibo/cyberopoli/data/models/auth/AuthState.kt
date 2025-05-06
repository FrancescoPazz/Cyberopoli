package com.unibo.cyberopoli.data.models.auth

sealed class AuthState {
    data object Authenticated : AuthState()
    data object AnonymousAuthenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}