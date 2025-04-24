package com.unibo.cyberopoli.ui.screens.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.RankingRepository
import com.unibo.cyberopoli.data.repositories.UserRepository

class RankingViewModel(
    private val userRepository: UserRepository,
    rankingRepository: RankingRepository = RankingRepository()
) : ViewModel() {
    private val currentUser: MutableLiveData<UserData?> = userRepository.currentUserLiveData
    val ranking: LiveData<List<UserData>> = rankingRepository.rankingLiveData

    init {
        loadUserData()
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    fun getMyRanking(): UserData? {
        val user = currentUser.value
        Log.d("RankingViewModel", "Current user: $user")
        return if (user != null) {
            ranking.value?.find { it.id == user.id }
        } else {
            null
        }
    }
}
