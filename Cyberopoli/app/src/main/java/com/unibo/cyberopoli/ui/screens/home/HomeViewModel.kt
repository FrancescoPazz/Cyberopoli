package com.unibo.cyberopoli.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val user: LiveData<User?> = userRepository.currentUserLiveData

    fun loadUserData() {
        viewModelScope.launch {
            userRepository.loadUserData()
        }
    }
}
