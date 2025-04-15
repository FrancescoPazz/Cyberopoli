package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.data.repositories.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    val user: LiveData<UserData?> = userRepository.userLiveData

    init {
        loadUserData()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    fun changeAvatar() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val avatarList =
            listOf("avatar_male_1", "avatar_male_2", "avatar_female_1", "avatar_female_2")

        val currentUser = auth.currentUser ?: return
        val currentAvatar = user.value?.profileImageUrl ?: avatarList.first()
        val currentIndex = avatarList.indexOf(currentAvatar)
        val nextIndex =
            if (currentIndex == -1 || currentIndex == avatarList.size - 1) 0 else currentIndex + 1
        val newAvatar = avatarList[nextIndex]
        db.collection("users").document(currentUser.uid).update("profileImageUrl", newAvatar)
            .addOnSuccessListener {
                userRepository.loadUserData()
            }
    }
}
