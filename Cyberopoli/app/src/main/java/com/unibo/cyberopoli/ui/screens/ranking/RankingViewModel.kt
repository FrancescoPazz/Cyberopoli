package com.unibo.cyberopoli.ui.screens.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.profile.RankingRepository
import com.unibo.cyberopoli.data.repositories.profile.UserRepository

class RankingViewModel(
    private val userRepo: UserRepository, rankingRepository: RankingRepository
) : ViewModel() {
    private val currentUser: MutableLiveData<User?> = userRepo.currentUserLiveData
    val ranking: LiveData<List<User>> = rankingRepository.rankingLiveData

    init {
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepo.loadUserData()
    }

    fun getMyRanking(): User? {
        return currentUser.value
    }
}
