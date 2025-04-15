package com.unibo.cyberopoli.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.data.models.UserData

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val userLiveData: MutableLiveData<UserData?> = MutableLiveData()

    fun loadUserData() {
        val currentUser = auth.currentUser
        Log.d("TestMATTO UserRepository", "currentUser: $currentUser")
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        Log.e("TestMATTO UserRepository", "Errore nel caricamento dei dati", error)
                        return@addSnapshotListener
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(UserData::class.java)
                        userLiveData.value = userData
                        Log.d("TestMATTO UserRepository", "Dati utente aggiornati: $userData")
                    }
                }
        }
    }

    fun clearUserData() {
        userLiveData.value = null
    }
}
