package com.unibo.cyberopoli.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unibo.cyberopoli.data.models.RankingUser
import com.unibo.cyberopoli.data.models.UserData

class RankingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val rankingLiveData = MutableLiveData<List<RankingUser>>()

    fun loadRanking() {
        db.collection("users").orderBy("score", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val list = result.mapIndexed { index, doc ->
                    val user = doc.toObject(UserData::class.java)
                    RankingUser(
                        userId = user.userId ?: "",
                        rank = index + 1,
                        name = user.name ?: "",
                        surname = user.surname ?: "",
                        score = user.score ?: 0,
                        avatarUrl = user.profileImageUrl ?: ""
                    )
                }
                rankingLiveData.value = list
                Log.d("TestMATTO RankingRepository", "Ranking caricato: $list")
            }.addOnFailureListener {
                rankingLiveData.value = emptyList()
            }
    }
}
