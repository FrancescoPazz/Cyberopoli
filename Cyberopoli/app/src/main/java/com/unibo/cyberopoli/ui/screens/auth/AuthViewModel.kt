package com.unibo.cyberopoli.ui.screens.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.AuthResponse
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.repositories.auth.AuthRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository, private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    init {
        viewModelScope.launch {
            authRepository.authState().collect { state ->
                _authState.postValue(state)
                if (state is AuthState.Authenticated) {
                    userRepository.loadUserData()
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signIn(email, password).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepository.loadUserData()
                        _authState.value = AuthState.Authenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message)
                    }
                }
            }
        }
    }

    fun signUp(
        name: String?, surname: String?, username: String, email: String, password: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUp(name, surname, username, email, password).collect { resp ->
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
            authRepository.signInWithGoogle(context).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepository.loadUserData()
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
            authRepository.signInAnonymously(name).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        userRepository.loadUserData()
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
            val ok = authRepository.resetPassword(email).single()
            if (ok is AuthResponse.Success) {
                _authState.value = AuthState.Unauthenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error("Impossible to reset password")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val ok = authRepository.signOut().single()
            if (ok is AuthResponse.Success) {
                userRepository.clearUserData()
                _authState.value = AuthState.Unauthenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error("Logout error")
            }
        }
    }
}