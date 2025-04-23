package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        val cu = currentUser.value
        if (cu !is CurrentUser.Registered) return

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val avatarList = listOf(
            "avatar_male_1",
            "avatar_male_2",
            "avatar_female_1",
            "avatar_female_2"
        )

        val currentAvatar = cu.data.profileImageUrl ?: avatarList.first()
        val currentIndex = avatarList.indexOf(currentAvatar).takeIf { it >= 0 } ?: 0
        val nextIndex = if (currentIndex == avatarList.lastIndex) 0 else currentIndex + 1
        val newAvatar = avatarList[nextIndex]

        db.collection("users")
            .document(userId)
            .update("profileImageUrl", newAvatar)
            .addOnSuccessListener {
                userRepository.loadUserData()
            }
    }
}
