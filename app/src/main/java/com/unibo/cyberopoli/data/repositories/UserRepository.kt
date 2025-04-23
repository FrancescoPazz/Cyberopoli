package com.unibo.cyberopoli.data.repositories

import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.CurrentUser

class UserRepository(
) {
    val currentUserLiveData = MutableLiveData<CurrentUser?>()

    fun loadUserData() {
    }

    fun clearUserData() {
        currentUserLiveData.value = null
    }
}
