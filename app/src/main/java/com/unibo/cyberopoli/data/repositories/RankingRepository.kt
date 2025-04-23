package com.unibo.cyberopoli.data.repositories

import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.UserData

class RankingRepository(
) {
    val rankingLiveData = MutableLiveData<List<UserData>>()

    fun loadRanking() {
    }
}
