package com.unibo.cyberopoli.data.repositories

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.unibo.cyberopoli.ui.screens.auth.AuthResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.security.MessageDigest
import java.util.UUID

private const val GOOGLE_SERVER_CLIENT_ID =
    "965652282511-hveojtrsgklpr52hbi54qg9ct477llmh.apps.googleusercontent.com"

class AuthRepository(
    private val client: SupabaseClient
) {
    fun currentUser(): UserInfo? = client.auth.currentUserOrNull()

    // Login
    fun signIn(email: String, password: String): Flow<AuthResponse> = flow {
        Log.d("AuthRepository", "Attempting to log in with email: $email")
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging in: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Login error"))
        }
    }

    fun signUp(email: String, password: String): Flow<AuthResponse> = flow {
        try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing up: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Sign up error"))
        }
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun signInWithGoogle(context: Context): Flow<AuthResponse> = flow {
        val hashedNonce = createNonce()

        val googleIdOption = GetGoogleIdOption.Builder().setServerClientId(GOOGLE_SERVER_CLIENT_ID)
            .setNonce(hashedNonce).setAutoSelectEnabled(false).setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        val credentialManager = CredentialManager.create(context)

        try {
            val result = credentialManager.getCredential(
                context = context, request = request
            )

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error logging in with Google: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Google login error"))
        }
    }

    fun signInAnonymously(name: String): Flow<AuthResponse> = flow {
        try {
            client.auth.signInAnonymously(
                data = JsonObject(mapOf("name" to JsonPrimitive(name)))
            )
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing in anonymously: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Anonymous login error"))
        }
    }

    fun signOut(): Flow<AuthResponse> = flow {
        try {
            client.auth.signOut()
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Sign out error"))
        }
    }

    fun resetPassword(email: String): Flow<AuthResponse> = flow {
        try {
            client.auth.resetPasswordForEmail(email)
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error resetting password: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Reset password error"))
        }
    }

    fun deleteAnonymousUserAndSignOut(): Flow<AuthResponse> = flow {
        try {
            val session: UserSession? = client.auth.currentSessionOrNull()
            session?.user?.id?.let { userId ->
                client.auth.admin.deleteUser(userId)
            }
            client.auth.signOut()
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Error deleting user"))
        }
    }
}