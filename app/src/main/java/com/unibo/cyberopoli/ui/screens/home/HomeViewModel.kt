package com.unibo.cyberopoli.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    val user: LiveData<UserData?> = userRepository.userLiveData

    init {
        loadUserData()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }
}
