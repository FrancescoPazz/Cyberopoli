package com.unibo.cyberopoli.ui.screens.ranking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.ranking.RankingRepository
import kotlinx.coroutines.launch

class RankingViewModel(
    private val rankingRepository: RankingRepository,
) : ViewModel() {
    val rankingUsers: LiveData<List<User>?> = rankingRepository.rankingUsersLiveData

    init {
        viewModelScope.launch {
            rankingRepository.loadRanking()
        }
    }
}
