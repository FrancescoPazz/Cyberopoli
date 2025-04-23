package com.unibo.cyberopoli.ui.screens.home

import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.repositories.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    init {
        loadUserData()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }
}
