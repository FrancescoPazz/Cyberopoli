package com.unibo.cyberopoli.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.user.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val user: LiveData<User?> = userRepository.currentUserLiveData

    fun loadUserData() {
        userRepository.loadUserData()
    }
}
