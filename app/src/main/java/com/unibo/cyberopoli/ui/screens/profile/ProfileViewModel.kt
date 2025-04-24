package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepo: UserRepository
) : ViewModel() {

    val user: LiveData<UserData?> = userRepo.currentUserLiveData

    fun changeAvatar() {
        viewModelScope.launch {
            userRepo.changeAvatar()
        }
    }
}
