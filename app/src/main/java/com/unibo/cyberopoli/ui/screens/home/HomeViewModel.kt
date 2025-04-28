package com.unibo.cyberopoli.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.profile.UserRepository

class HomeViewModel(
    private val userRepo: UserRepository
) : ViewModel() {
    val user: LiveData<UserData?> = userRepo.currentUserLiveData

    fun loadUserData() {
        userRepo.loadUserData()
    }
}
