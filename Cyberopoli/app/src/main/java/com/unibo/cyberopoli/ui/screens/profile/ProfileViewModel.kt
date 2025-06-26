package com.unibo.cyberopoli.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import com.unibo.cyberopoli.util.UsageStatsHelper
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val usageStatsHelper: UsageStatsHelper,
) : ViewModel() {
    val user: LiveData<User?> = userRepository.currentUserLiveData

    private val _topAppsUsage = mutableStateListOf<Pair<String, Double>>()
    val topAppsUsage: SnapshotStateList<Pair<String, Double>> = _topAppsUsage

    private val _gameHistories = mutableStateListOf<GameHistory>()
    val gameHistories: SnapshotStateList<GameHistory> = _gameHistories

    init {
        viewModelScope.launch {
            Log.d("TESTONE", "ProfileViewModel init called")
            getTopUsedApps()
            getGameHistory()
        }
    }

    fun changeAvatar() {
        viewModelScope.launch {
            userRepository.changeAvatar()
        }
    }

    private suspend fun getTopUsedApps() {
        try {
            Log.d("TESTONE", "getTopUsedApps called")
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
            Log.d("TESTONE", "getGameHistory called")
            _gameHistories.addAll(gameRepository.getGamesHistory())
            Log.d("TESTONE", "Game history size: $_gameHistories")
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            userRepository.loadUserData()
        }
    }
}
