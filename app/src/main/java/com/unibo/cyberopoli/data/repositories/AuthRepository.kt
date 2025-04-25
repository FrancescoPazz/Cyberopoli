package com.unibo.cyberopoli.data.repositories

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.ui.screens.auth.AuthResponse
import com.unibo.cyberopoli.ui.screens.auth.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.security.MessageDigest
import java.util.UUID

private const val GOOGLE_SERVER_CLIENT_ID =
    "965652282511-hveojtrsgklpr52hbi54qg9ct477llmh.apps.googleusercontent.com"

class AuthRepository(
    private val supabase: SupabaseClient
) {
    fun authStateFlow(): Flow<AuthState> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Initializing -> AuthState.Loading
            is SessionStatus.Authenticated -> AuthState.Authenticated
            is SessionStatus.NotAuthenticated, is SessionStatus.RefreshFailure -> AuthState.Unauthenticated
        }
    }

    // Login
    fun signIn(email: String, password: String): Flow<AuthResponse> = flow {
        Log.d("AuthRepository", "Attempting to log in with email: $email")
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging in: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Login error"))
        }
    }

    // Sign up
    fun signUp(
        email: String, password: String, name: String, surname: String
    ): Flow<AuthResponse> = flow {
        try {
            val signUpResult = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = signUpResult?.id
                ?: throw IllegalStateException("Sign-up success but user.id is null")

            Log.d("AuthRepository", "Sign-up avvenuto, userId = $userId")

            val user = UserData(
                id = userId,
                email = email,
                firstName = name,
                lastName = surname,
                displayName = "$name $surname",
                isGuest = false
            )
            supabase.from("users").upsert(user)

            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error during sign up or database insertion: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Sign up or database insertion error"))
        }
    }


    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        Log.d("AuthRepository", "Raw nonce: $rawNonce")
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // Google Sign In
    fun signInWithGoogle(context: Context): Flow<AuthResponse> = flow {
        val hashedNonce = createNonce()
        Log.d("AuthRepository", "Hashed nonce: $hashedNonce")
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

            supabase.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }

            supabase.auth.sessionStatus.filterIsInstance<SessionStatus.Authenticated>().first()

            val session = supabase.auth.currentSessionOrNull()
                ?: throw IllegalStateException("No session found after Google login")
            val userId = session.user?.id!!
            val email = session.user?.email
            val fullName = session.user?.userMetadata?.get("full_name").toString().trim('"')
            val name = fullName.substringBefore(" ")
            val surname = fullName.substringAfter(" ", "")

            val user = UserData(
                id = userId, email = email, firstName = name, lastName = surname, isGuest = false
            )
            supabase.from("users").upsert(user)

            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error logging in with Google: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Google login error"))
        }
    }

    // Sign in anonymously
    fun signInAnonymously(name: String, surname: String = ""): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signInAnonymously(
                data = JsonObject(mapOf("name" to JsonPrimitive(name)))
            )
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing in anonymously: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Anonymous login error"))
        }

        // Database insertion
        try {
            val session = supabase.auth.currentSessionOrNull()
                ?: throw IllegalStateException("No session found after Google login")
            val userId = session.user?.id!!
            val user = UserData(
                id = userId,
                firstName = name,
                lastName = surname,
                isGuest = true,
            )
            supabase.from("users").insert(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error inserting anonymous user into database: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Database insertion error"))
        }
    }

    // Sign out
    fun signOut(): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signOut()
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Sign out error"))
        }
    }

    // Reset password
    fun resetPassword(email: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.resetPasswordForEmail(email)
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error resetting password: ${e.message}")
            emit(AuthResponse.Failure(e.message ?: "Reset password error"))
        }
    }

    // Delete anonymous user
    fun deleteAnonymousUserAndSignOut(): Flow<AuthResponse> = flow {
        try {
            val session: UserSession? = supabase.auth.currentSessionOrNull()
            session?.user?.id?.let { userId ->
                supabase.auth.admin.deleteUser(userId)
            }

            // Delete user from the database
            supabase.from("users").delete {
                filter {
                    supabase.auth.currentUserOrNull()?.id?.let { eq("id", it) }
                }
            }

            supabase.auth.signOut()
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Error deleting user"))
        }
    }
}