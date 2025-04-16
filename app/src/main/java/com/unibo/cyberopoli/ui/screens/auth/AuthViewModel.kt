package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository
import java.security.MessageDigest
import java.util.UUID

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    val user: LiveData<UserData?> = userRepository.userLiveData

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
            userRepository.loadUserData()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(context: Context, email: String, password: String) {
        Log.d("TestMATTO AuthViewModel", "Login called with email: $email and password: $password")
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error(context.getString(R.string.empty_fields))
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Authenticated
                userRepository.loadUserData()
            } else {
                _authState.value = AuthState.Error(
                    task.exception?.message ?: context.getString(R.string.login_failed)
                )
            }
        }
    }

    fun signUp(context: Context, email: String, password: String, name: String, surname: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error(context.getString(R.string.empty_fields))
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = auth.currentUser
                val userId = user?.uid ?: return@addOnCompleteListener

                val userData = hashMapOf(
                    "userId" to userId,
                    "email" to email.trim(),
                    "name" to name.trim(),
                    "surname" to surname.trim(),
                    "level" to 1,
                    "score" to 0,
                    "creationDate" to Timestamp.now(),
                    "profileImageUrl" to null,
                    "totalGames" to 0,
                    "totalWins" to 0,
                    "totalMedals" to 0,
                    "bestScore" to 0,
                    "averageScore" to 0,
                )

                db.collection("users").document(userId).set(userData).addOnSuccessListener {
                    _authState.value = AuthState.Authenticated
                }.addOnFailureListener { e ->
                    _authState.value = AuthState.Error(
                        e.message ?: context.getString(R.string.signup_failed)
                    )
                }
            } else {
                _authState.value = AuthState.Error(
                    task.exception?.message ?: context.getString(R.string.login_failed)
                )
            }
        }
    }

    private fun createNonce() : String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInWithGoogle(context: Context) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)

            val credential = result.credential
            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider
                            .getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )

                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    _authState.value = AuthState.Authenticated
                                    userRepository.loadUserData()
                                } else {
                                    _authState.value = AuthState.Error(
                                        it.exception?.message ?: context.getString(R.string.login_failed)
                                    )
                                }
                            }
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error(
                            e.message ?: context.getString(R.string.login_failed)
                        )
                    }
                }
            }

        } catch (e: Exception) {
            _authState.value = AuthState.Error(
                e.message ?: context.getString(R.string.login_failed)
            )
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        userRepository.clearUserData()
    }
}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}