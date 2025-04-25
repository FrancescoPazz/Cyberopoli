package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.repositories.AuthRepository
import com.unibo.cyberopoli.data.repositories.UserRepository
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepo: AuthRepository, private val userRepo: UserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    init {
        viewModelScope.launch {
            authRepo.authStateFlow().collect { state ->
                _authState.postValue(state)
                if (state is AuthState.Authenticated) {
                    userRepo.loadUserData()
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepo.signIn(email, password).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepo.loadUserData()
                        _authState.value = AuthState.Authenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message)
                    }
                }
            }
        }
    }

    fun signUp(email: String, password: String, name: String, surname: String) {
        if (name.isBlank() && surname.isBlank()) {
            _authState.value = AuthState.Error("Name or surname required")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepo.signUp(email, password, name, surname).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        _authState.value = AuthState.Unauthenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message)
                    }
                }
            }
        }
    }

    fun loginGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepo.signInWithGoogle(context).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepo.loadUserData()
                        _authState.value = AuthState.Authenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message)
                    }
                }
            }
        }
    }

    fun loginAnonymously(name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepo.signInAnonymously(name).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepo.loadUserData()
                        _authState.value = AuthState.AnonymousAuthenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message)
                    }
                }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            val ok = authRepo.resetPassword(email).single()
            if (ok is AuthResponse.Success) {
                _authState.value = AuthState.Unauthenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error("Impossible to reset password")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val ok = authRepo.signOut().single()
            if (ok is AuthResponse.Success) {
                userRepo.clearUserData()
                _authState.value = AuthState.Unauthenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error("Logout error")
            }
        }
    }

    fun deleteAnonymousUserAndSignOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val ok = authRepo.deleteAnonymousUserAndSignOut().single()
            if (ok is AuthResponse.Success) {
                userRepo.clearUserData()
                _authState.value = AuthState.Unauthenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error("Error deleting user")
            }
        }
    }
}