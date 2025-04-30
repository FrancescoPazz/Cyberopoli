package com.unibo.cyberopoli.domain.repository

import android.content.Context
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.screens.auth.AuthResponse
import com.unibo.cyberopoli.ui.screens.auth.AuthState
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun authState(): Flow<AuthState>

    fun signIn(email: String, password: String): Flow<AuthResponse>

    fun signUp(email: String, password: String, firstName: String, lastName: String): Flow<AuthResponse>

    fun signInAnonymously(name: String): Flow<AuthResponse>

    fun signInWithGoogle(context: Context): Flow<AuthResponse>

    fun signOut(): Flow<AuthResponse>

    fun resetPassword(email: String): Flow<AuthResponse>

    suspend fun currentUser(): User?
}
