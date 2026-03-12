package com.unibo.cyberopoli.ui.screens.ranking.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.repositories.ranking.RankingRepository
import kotlinx.coroutines.launch

class RankingViewModel(
    private val rankingRepository: RankingRepository,
) : ViewModel() {
    private val _rankingUsers = mutableStateListOf<User>()
    val rankingUsers: SnapshotStateList<User> = _rankingUsers

    init {
        viewModelScope.launch {
            rankingRepository.loadRanking()?.let { _rankingUsers.addAll(it) }
        }
    }
}
