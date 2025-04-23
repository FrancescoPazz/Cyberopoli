package com.unibo.cyberopoli.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.data.models.auth.CurrentUser
import com.unibo.cyberopoli.data.models.auth.GuestData
import com.unibo.cyberopoli.data.models.auth.UserData

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUserLiveData = MutableLiveData<CurrentUser?>()

    fun loadUserData() {
        val user = auth.currentUser ?: run {
            currentUserLiveData.value = null
            return
        }

        if (user.isAnonymous) {
            db.collection("guestUsers")
                .document(user.uid)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        currentUserLiveData.value = null
                        return@addSnapshotListener
                    }
                    snap?.toObject(GuestData::class.java)
                        ?.let { currentUserLiveData.value = CurrentUser.Guest(it) }
                }
        } else {
            db.collection("users")
                .document(user.uid)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        currentUserLiveData.value = null
                        return@addSnapshotListener
                    }
                    snap?.toObject(UserData::class.java)
                        ?.let {
                            currentUserLiveData.value = CurrentUser.Registered(it)
                        }
                }
        }
    }

    fun clearUserData() {
        currentUserLiveData.value = null
    }
}
