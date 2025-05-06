package com.unibo.cyberopoli.ui.screens.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.ranking.RankingRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository

class RankingViewModel(
    private val userRepository: UserRepository, rankingRepository: RankingRepository
) : ViewModel() {
    private val currentUser: MutableLiveData<User?> = userRepository.currentUserLiveData
    val ranking: LiveData<List<User>> = rankingRepository.rankingLiveData

    init {
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    fun getMyRanking(): User? {
        return currentUser.value
    }
}
