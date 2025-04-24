package com.unibo.cyberopoli.ui.screens.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.repositories.RankingRepository
import com.unibo.cyberopoli.data.repositories.UserRepository

class RankingViewModel(
    private val userRepo: UserRepository,
    rankingRepository: RankingRepository
) : ViewModel() {
    private val currentUser: MutableLiveData<UserData?> = userRepo.currentUserLiveData
    val ranking: LiveData<List<UserData>> = rankingRepository.rankingLiveData

    init {
        rankingRepository.loadRanking()
    }

    fun loadUserData() {
        userRepo.loadUserData()
    }

    fun getMyRanking(): UserData? {
        return currentUser.value
    }
}
