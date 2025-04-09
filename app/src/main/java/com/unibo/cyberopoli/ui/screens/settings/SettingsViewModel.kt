package com.unibo.cyberopoli.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.unibo.cyberopoli.data.models.Theme
import com.unibo.cyberopoli.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: Theme)

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val state = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    fun changeTheme(newTheme: Theme) = viewModelScope.launch {
        repository.setTheme(newTheme)
    }

    fun updatePasswordWithOldPassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onError("Utente non autenticato.")
            return@launch
        }

        val email = currentUser.email
        if (email.isNullOrEmpty()) {
            onError("Email non disponibile per l'utente.")
            return@launch
        }

        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    currentUser.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(updateTask.exception?.message ?: "Errore nel cambio password.")
                            }
                        }
                } else {
                    onError(reauthTask.exception?.message ?: "Errore nella verifica della vecchia password.")
                }
            }
    }

}