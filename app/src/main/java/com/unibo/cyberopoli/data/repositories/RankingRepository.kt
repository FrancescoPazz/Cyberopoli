package com.unibo.cyberopoli.data.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unibo.cyberopoli.data.models.auth.UserData

class RankingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val rankingLiveData = MutableLiveData<List<UserData>>()

    fun loadRanking() {
        db.collection("users").orderBy("score", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val list = result.mapIndexed { index, doc ->
                    val user = doc.toObject(UserData::class.java)
                    UserData(
                        userId = user.userId ?: "",
                        level = index + 1,
                        name = user.name ?: "",
                        surname = user.surname ?: "",
                        score = user.score ?: 0,
                        profileImageUrl = user.profileImageUrl ?: ""
                    )
                }
                rankingLiveData.value = list
            }.addOnFailureListener {
                rankingLiveData.value = emptyList()
            }
    }
}
