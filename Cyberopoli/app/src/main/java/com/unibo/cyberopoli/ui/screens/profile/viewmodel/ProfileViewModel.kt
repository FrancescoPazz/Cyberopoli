package com.unibo.cyberopoli.ui.screens.profile.viewmodel

import android.util.Log
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.unibo.cyberopoli.data.models.auth.User
import androidx.compose.runtime.mutableStateListOf
import com.unibo.cyberopoli.data.models.game.GameHistory
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import com.unibo.cyberopoli.util.UsageStatsHelper

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val usageStatsHelper: UsageStatsHelper,
) : ViewModel() {
    val user = mutableStateOf<User?>(null)

    private val _topAppsUsage = mutableStateListOf<Pair<String, Double>>()
    val topAppsUsage: SnapshotStateList<Pair<String, Double>> = _topAppsUsage

    private val _gameHistories = mutableStateListOf<GameHistory>()
    val gameHistories: SnapshotStateList<GameHistory> = _gameHistories

    init {
        viewModelScope.launch {
            getTopUsedApps()
            getGameHistory()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                user.value = userRepository.getUser()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user data: ${e.message}")
            }
        }
    }

    fun changeAvatar() {
        viewModelScope.launch {
            userRepository.changeAvatar()
        }
    }

    private suspend fun getTopUsedApps() {
        try {
            val appsUsage = usageStatsHelper.getTopUsedApps(5)
            _topAppsUsage.addAll(appsUsage)
        } catch (e: Exception) {
            _topAppsUsage.clear()
        }
    }

    fun updateUserInfo(
        newName: String?,
        newSurname: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                userRepository.updateUserInfo(newName, newSurname)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error updating user info")
            }
        }
    }

    fun updatePasswordWithOldPassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) = viewModelScope.launch {
        try {
            userRepository.changePassword(oldPassword, newPassword)
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Error changing password")
        }
    }

    private fun getGameHistory() {
        viewModelScope.launch {
            _gameHistories.addAll(gameRepository.getGamesHistory())
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            userRepository.getUser()
        }
    }
}
