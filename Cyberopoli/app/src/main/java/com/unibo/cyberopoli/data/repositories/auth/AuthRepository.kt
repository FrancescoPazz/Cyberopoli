package com.unibo.cyberopoli.data.repositories.auth

import java.util.UUID
import android.content.Context
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import androidx.credentials.CredentialManager
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.coroutines.flow.filterIsInstance
import androidx.credentials.GetCredentialRequest
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.auth.providers.Google
import com.unibo.cyberopoli.data.models.auth.AuthState
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.OTP
import com.unibo.cyberopoli.data.models.auth.AuthResponse
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

private const val GOOGLE_SERVER_CLIENT_ID =
    "965652282511-hveojtrsgklpr52hbi54qg9ct477llmh.apps.googleusercontent.com"

const val USERS_TABLE = "users"

class AuthRepository(
    private val supabase: SupabaseClient,
) : IAuthRepository {
    override fun authState(): Flow<AuthState> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Initializing -> AuthState.Loading
            is SessionStatus.Authenticated -> if (supabase.auth.currentUserOrNull()?.userMetadata?.get(
                    "email",
                ) != null
            ) {
                AuthState.Authenticated
            } else {
                AuthState.AnonymousAuthenticated
            }

            is SessionStatus.NotAuthenticated, is SessionStatus.RefreshFailure -> AuthState.Unauthenticated
        }
    }

    override fun signIn(
        email: String,
        password: String,
    ): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Login error"))
        }
    }

    override fun signUp(
        name: String?,
        surname: String?,
        username: String,
        email: String,
        password: String,
    ): Flow<AuthResponse> = flow {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            emit(AuthResponse.Failure("Username, email and password cannot be empty"))
            return@flow
        }

        val takenUsers = supabase.from(USERS_TABLE).select {
            filter {
                ilike("username", username)
            }
        }.decodeList<User>()
        if (takenUsers.isNotEmpty()) {
            emit(AuthResponse.Failure("Username already in use"))
            return@flow
        }

        val takenEmails = supabase.from(USERS_TABLE).select {
            filter {
                ilike("email", email)
            }
        }.decodeList<User>()
        if (takenEmails.isNotEmpty()) {
            emit(AuthResponse.Failure("Already registered email"))
            return@flow
        }

        try {
            val result = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userId =
                result?.id ?: throw IllegalStateException("Registration successful but userId null")

            val user = User(
                id = userId,
                name = name,
                surname = surname,
                username = username,
                email = email,
            )
            supabase.from(USERS_TABLE).upsert(user)

            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Error during registration"))
        }
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    override fun signInWithGoogle(context: Context): Flow<AuthResponse> = flow {
        val nonce = createNonce()
        val option =
            GetGoogleIdOption.Builder().setServerClientId(GOOGLE_SERVER_CLIENT_ID).setNonce(nonce)
                .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val manager = CredentialManager.create(context)

        try {
            val cred = manager.getCredential(context, request)
            val tokenCred = GoogleIdTokenCredential.createFrom(cred.credential.data)
            val idToken = tokenCred.idToken

            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            supabase.auth.sessionStatus.filterIsInstance<SessionStatus.Authenticated>().first()

            val session = supabase.auth.currentSessionOrNull()
                ?: throw IllegalStateException("No session after Google login")
            val info = session.user!!
            val fullName = info.userMetadata?.get("full_name").toString().trim('"')
            val (fn, ln) = fullName.split(" ", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
            val username = info.email!!.split("@")[0]

            val user = User(
                id = info.id,
                name = fn,
                surname = ln,
                username = username,
                email = info.email,
            )
            supabase.from(USERS_TABLE).upsert(user)
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Google login error"))
        }
    }

    override fun signInAnonymously(username: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signInAnonymously(
                data = JsonObject(mapOf("name" to JsonPrimitive(username))),
            )
            val session = supabase.auth.currentSessionOrNull()
                ?: throw IllegalStateException("No session after anonymous login")
            val userId = session.user!!.id
            val user = User(
                id = userId,
                email = null,
                username = username,
                surname = null,
                isGuest = true,
            )
            supabase.from(USERS_TABLE).upsert(user)
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Anonymous login error"))
        }
    }

    override fun signOut(): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signOut()
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Sign out error"))
        }
    }

    override fun resetPassword(email: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.resetPasswordForEmail(email)
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Reset password error"))
        }
    }

    override fun sendOtp(email: String, otp: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = email, token = otp)
            if (supabase.auth.currentUserOrNull() == null) {
                supabase.auth.signInWith(OTP) {
                    this.email = email
                }
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Send OTP error"))
        }
    }

    override fun changeForgottenPassword(newPassword: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.updateUser {
                password = newPassword
            }
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            emit(AuthResponse.Failure(e.message ?: "Error changing password"))
        }
    }

    override suspend fun currentUser(): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        val data = session.user!!
        return User(
            id = data.id,
            username = data.userMetadata?.get("full_name")?.toString()?.trim('"') ?: data.id,
            isGuest = supabase.from(USERS_TABLE).select {
                filter { eq("id", data.id) }
            }.decodeList<User>().firstOrNull()?.isGuest ?: false,
            email = data.email,
            avatarUrl = null,
        )
    }
}
