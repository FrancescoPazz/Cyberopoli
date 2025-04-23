package com.unibo.cyberopoli.ui.screens.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.CurrentUser
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.RankingRepository
import com.unibo.cyberopoli.data.repositories.UserRepository

class RankingViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val rankingRepository: RankingRepository = RankingRepository()
) : ViewModel() {
    private val currentUser: LiveData<CurrentUser?> = userRepository.currentUserLiveData
    val ranking: LiveData<List<UserData>> = rankingRepository.rankingLiveData

    init {
        loadUserData()
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    fun getMyRanking(): UserData? {
        val cu = currentUser.value
        if (cu !is CurrentUser.Registered) return null
        Log.d("TestMATTO RankingViewModel", "Ranking: ${ranking.value}")
        val currentUserId = cu.data.userId
        Log.d("TestMATTO RankingViewModel", "Current user ID: $currentUserId")
        return ranking.value?.find { it.userId == currentUserId }
    }
}
