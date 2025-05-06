package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val user: LiveData<User?> = userRepository.currentUserLiveData

    fun changeAvatar() {
        viewModelScope.launch {
            userRepository.changeAvatar()
        }
    }
}
