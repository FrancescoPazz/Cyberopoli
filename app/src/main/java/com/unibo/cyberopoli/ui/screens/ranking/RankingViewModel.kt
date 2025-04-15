package com.unibo.cyberopoli.ui.screens.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.RankingUser
import com.unibo.cyberopoli.data.models.UserData
import com.unibo.cyberopoli.data.repositories.RankingRepository
import com.unibo.cyberopoli.data.repositories.UserRepository

class RankingViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val rankingRepository: RankingRepository = RankingRepository()
) : ViewModel() {
    val user: LiveData<UserData?> = userRepository.userLiveData
    val ranking: LiveData<List<RankingUser>> = rankingRepository.rankingLiveData

    init {
        loadUserData()
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    fun getMyRanking(): RankingUser? {
        Log.d("TestMATTO RankingViewModel", "Ranking: ${ranking.value}")
        val currentUserId = user.value?.userId
        Log.d("TestMATTO RankingViewModel", "Current user ID: $currentUserId")
        return ranking.value?.find { it.userId == currentUserId }
    }
}
