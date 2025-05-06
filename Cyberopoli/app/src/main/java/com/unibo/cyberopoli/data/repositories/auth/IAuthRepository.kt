package com.unibo.cyberopoli.data.repositories.auth

import android.content.Context
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.auth.AuthResponse
import com.unibo.cyberopoli.data.models.auth.AuthState
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun authState(): Flow<AuthState>

    fun signIn(email: String, password: String): Flow<AuthResponse>

    fun signUp(
        name: String?, surname: String?, username: String, email: String, password: String
    ): Flow<AuthResponse>

    fun signInAnonymously(username: String): Flow<AuthResponse>

    fun signInWithGoogle(context: Context): Flow<AuthResponse>

    fun signOut(): Flow<AuthResponse>

    fun resetPassword(email: String): Flow<AuthResponse>

    suspend fun currentUser(): User?
}
