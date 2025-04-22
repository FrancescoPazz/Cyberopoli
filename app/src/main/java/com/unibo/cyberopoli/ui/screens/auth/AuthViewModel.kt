package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository
import com.unibo.cyberopoli.ui.contracts.AuthState

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

    private fun checkAuthStatus() {
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

    fun signInAnonymously(name: String, onComplete: (Boolean, String?) -> Unit) {
        _authState.value = AuthState.Loading
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser!!.uid
                val guestData = hashMapOf(
                    "uid" to uid, "name" to name.trim(), "creationDate" to Timestamp.now()
                )
                FirebaseFirestore.getInstance().collection("guestUsers").document(uid)
                    .set(guestData).addOnSuccessListener {
                        _authState.value = AuthState.Anonymous
                        onComplete(true, uid)
                    }.addOnFailureListener { e ->
                        _authState.value = AuthState.Error(e.message ?: "Guest error")
                        onComplete(false, null)
                    }
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Guest error")
                onComplete(false, null)
            }
        }
    }

    fun deleteAnonymousUserAndSignOut() {
        val user = auth.currentUser
        if (user != null && user.isAnonymous) {
            user.delete()
        }
        auth.signOut()
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        userRepository.clearUserData()
    }

    fun sendPasswordResetEmail(context: Context, email: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error(context.getString(R.string.empty_email_warning))
            return
        }

        _authState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Unauthenticated
            } else {
                _authState.value = AuthState.Error(
                    task.exception?.message ?: context.getString(R.string.reset_password_failed)
                )
            }
        }
    }
}