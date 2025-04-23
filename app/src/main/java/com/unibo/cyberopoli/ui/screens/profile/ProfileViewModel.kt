package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.CurrentUser
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val currentUser: LiveData<CurrentUser?> = userRepository.currentUserLiveData

    val userData = MediatorLiveData<UserData?>().apply {
        addSource(currentUser) { cu ->
            value = (cu as? CurrentUser.Registered)?.data
        }
    }

    init {
        userRepository.loadUserData()
    }

    fun changeAvatar() {
    }
}
