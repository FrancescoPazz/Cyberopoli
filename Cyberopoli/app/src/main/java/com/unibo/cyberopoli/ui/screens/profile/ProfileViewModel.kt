package com.unibo.cyberopoli.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GameHistory
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    val user: LiveData<User?> = userRepository.currentUserLiveData

    private val _gameHistories: MutableLiveData<List<GameHistory>> = MutableLiveData()
    val gameHistories: LiveData<List<GameHistory>> = _gameHistories

    fun changeAvatar() {
        viewModelScope.launch {
            userRepository.changeAvatar()
        }
    }

    fun getGameHistory() {
        viewModelScope.launch {
            gameRepository.getGamesHistory()
        }
    }
}
