package com.unibo.cyberopoli.ui.screens.profile

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

    private val _topAppsUsage: MutableLiveData<List<Pair<String, Double>>> = MutableLiveData()
    val topAppsUsage: LiveData<List<Pair<String, Double>>> = _topAppsUsage

    private val _gameHistories: MutableLiveData<List<GameHistory>> = MutableLiveData()
    val gameHistories: LiveData<List<GameHistory>> = _gameHistories

    init {
        viewModelScope.launch {
            getTopUsedApps()
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
            _topAppsUsage.postValue(appsUsage)
        } catch (e: Exception) {
            _topAppsUsage.postValue(emptyList())
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

    fun getGameHistory() {
        viewModelScope.launch {
            gameRepository.getGamesHistory()
        }
    }
}
