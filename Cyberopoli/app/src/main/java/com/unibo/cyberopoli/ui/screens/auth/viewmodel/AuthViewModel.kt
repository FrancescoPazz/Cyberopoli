package com.unibo.cyberopoli.ui.screens.auth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.AuthErrorContext
import com.unibo.cyberopoli.data.models.auth.AuthResponse
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.repositories.auth.AuthRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState

    init {
        viewModelScope.launch {
            authRepository.authState().collect { state ->
                _authState.postValue(state)
            }
        }
    }

    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signIn(
                email.trim(), password
            ).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        _authState.value = AuthState.RegistrationSuccess
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message, AuthErrorContext.LOGIN)
                    }
                }
            }
        }
    }

    fun signUp(
        name: String?,
        surname: String?,
        username: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUp(
                name?.trim(), surname?.trim(), username.trim(), email.trim(), password
            ).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        _authState.value = AuthState.RegistrationSuccess
                    }

                    is AuthResponse.Failure -> {
                        _authState.value = AuthState.Error(resp.message, AuthErrorContext.SIGNUP)
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
                        _authState.value = AuthState.Authenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value =
                            AuthState.Error(resp.message, AuthErrorContext.GOOGLE_AUTH)
                    }
                }
            }
        }
    }

    fun loginAnonymously(name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInAnonymously(
                name.trim()
            ).collect { resp ->
                when (resp) {
                    is AuthResponse.Success -> {
                        _authState.value = AuthState.AnonymousAuthenticated
                    }

                    is AuthResponse.Failure -> {
                        _authState.value =
                            AuthState.Error(resp.message, AuthErrorContext.ANONYMOUS_LOGIN)
                    }
                }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            val resp = authRepository.resetPassword(
                email.trim()
            ).single()
            if (resp is AuthResponse.Success) {
                _authState.value = AuthState.Unauthenticated
            } else if (resp is AuthResponse.Failure) {
                _authState.value = AuthState.Error(resp.message, AuthErrorContext.PASSWORD_RESET)
            }
        }
    }

    private val emailForgotPassword = MutableLiveData("")
    fun sendOTPCode(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            val ok = authRepository.sendOtp(
                email.trim(), otp.trim()
            ).single()
            if (ok is AuthResponse.Success) {
                emailForgotPassword.value = email
                _authState.value = AuthState.Authenticated
                changeForgottenPassword(newPassword)
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error(
                    "Impossible to send OTP code",
                    AuthErrorContext.OTP_VERIFICATION
                )
            }
        }
    }

    private fun changeForgottenPassword(newPassword: String) {
        viewModelScope.launch {
            val ok = authRepository.changeForgottenPassword(newPassword).single()
            if (ok is AuthResponse.Success) {
                _authState.value = AuthState.Authenticated
            } else if (ok is AuthResponse.Failure) {
                _authState.value = AuthState.Error(
                    "Impossible to change password",
                    AuthErrorContext.PASSWORD_RESET
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val resp = authRepository.signOut().single()
            if (resp is AuthResponse.Success) {
                _authState.value = AuthState.Unauthenticated
            } else if (resp is AuthResponse.Failure) {
                _authState.value = AuthState.Error(resp.message)
            }
        }
    }
}
