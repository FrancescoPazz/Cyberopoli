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

    fun updateProfileImage(
        uri: Uri
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val newImageUrl = uri.toString()
            db.collection("users").document(currentUser.uid)
                .update("profileImageUrl", newImageUrl).addOnSuccessListener {
                    _user.value = _user.value?.copy(profileImageUrl = newImageUrl)
                }
        }
    }
}
