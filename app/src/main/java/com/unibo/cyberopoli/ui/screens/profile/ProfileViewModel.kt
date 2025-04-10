package com.unibo.cyberopoli.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.data.models.UserData

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<UserData?>()
    val user: MutableLiveData<UserData?> = _user

    init {
        if (auth.currentUser != null) {
            loadUserProfile()
        }
    }

    private val avatarList = listOf("avatar_male_1", "avatar_male_2", "avatar_female_1", "avatar_female_2")

    fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    Log.d("ProfileViewModelAIUTOOO", "Caricamento dati utente riuscito")
                    if (documentSnapshot.exists()) {
                        Log.d("ProfileViewModelAIUTOOO", "Documento utente esistente")
                        val userData = documentSnapshot.toObject(UserData::class.java)
                        if (userData != null) {
                            _user.value = userData
                            Log.d("ProfileViewModelAIUTOOO", "Dati utente: $userData")
                        }
                    }
                }
        }
    }

    fun changeAvatar() {
        val currentUser = auth.currentUser ?: return
        val currentAvatar = _user.value?.profileImageUrl ?: avatarList.first()
        val currentIndex = avatarList.indexOf(currentAvatar)
        val nextIndex = if (currentIndex == -1 || currentIndex == avatarList.size - 1) 0 else currentIndex + 1
        val newAvatar = avatarList[nextIndex]
        db.collection("users").document(currentUser.uid)
            .update("profileImageUrl", newAvatar)
            .addOnSuccessListener {
                _user.value = _user.value?.copy(profileImageUrl = newAvatar)
            }
    }
}
