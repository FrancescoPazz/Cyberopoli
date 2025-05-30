package com.unibo.cyberopoli.data.models.auth

sealed interface AuthResponse {
    data object Success : AuthResponse

    data class Failure(val message: String) : AuthResponse
}
