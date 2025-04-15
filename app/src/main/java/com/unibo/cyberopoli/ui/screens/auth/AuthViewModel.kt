package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.R

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(context: Context, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error(context.getString(R.string.empty_fields))
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Authenticated
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
                    "email" to email,
                    "name" to name,
                    "surname" to surname,
                    "level" to 1,
                    "score" to 0,
                    "creationDate" to Timestamp.now(),
                    "profileImageUrl" to null,
                    "totalGames" to 0,
                    "totalWins" to 0,
                    "totalMedals" to 0
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

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}