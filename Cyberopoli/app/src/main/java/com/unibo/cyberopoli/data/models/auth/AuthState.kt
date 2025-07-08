package com.unibo.cyberopoli.data.models.auth

enum class AuthErrorContext {
    LOGIN, SIGNUP, PASSWORD_RESET, GOOGLE_AUTH, ANONYMOUS_LOGIN, OTP_VERIFICATION, OTHER
}

sealed class AuthState {
    data object Authenticated : AuthState()

    data object AnonymousAuthenticated : AuthState()

    data object Unauthenticated : AuthState()

    data object Loading : AuthState()

    data object RegistrationSuccess : AuthState()

    data class Error(
        val message: String, val context: AuthErrorContext = AuthErrorContext.OTHER
    ) : AuthState()
}
