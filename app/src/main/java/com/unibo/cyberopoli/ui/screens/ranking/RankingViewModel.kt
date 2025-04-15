package com.unibo.cyberopoli.ui.screens.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unibo.cyberopoli.data.models.RankingUser
import com.unibo.cyberopoli.data.models.UserData

class RankingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _ranking = MutableLiveData<List<RankingUser>>()
    val ranking: LiveData<List<RankingUser>> = _ranking

    init {
        loadRanking()
    }

    fun loadRanking() {
        db.collection("users").orderBy("score", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val userDataList = result.toObjects(UserData::class.java)
                val rankingList = userDataList.mapIndexed { index, userData ->
                    RankingUser(
                        rank = index + 1,
                        name = userData.name ?: "",
                        surname = userData.surname ?: "",
                        score = userData.score ?: 0,
                        avatarUrl = userData.profileImageUrl ?: ""
                    )
                }
                _ranking.value = rankingList
            }.addOnFailureListener { exception ->
                _ranking.value = emptyList()
            }
    }
}
