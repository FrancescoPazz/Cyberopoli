package com.unibo.cyberopoli.ui.screens.lobby

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.launch

class LobbyViewModel(
    userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {

    val user: LiveData<User?> = userRepository.currentUserLiveData
    val lobby: LiveData<Lobby?> = lobbyRepository.currentLobbyLiveData
    val members: LiveData<List<LobbyMember>?> = lobbyRepository.currentMembersLiveData

    val isHost: LiveData<Boolean> = lobby.map { currentLobby ->
        currentLobby?.hostId == user.value?.id
    }

    val allReady: LiveData<Boolean> = members.map { currentMembers ->
        currentMembers?.all { it.isReady } ?: false
    }

    fun startLobbyFlow(lobbyId: String) {
        viewModelScope.launch {
            if (user.value == null) {
                Log.w("LobbyViewModel", "startLobbyFlow: User is null")
                return@launch
            }
            try {
                lobbyRepository.createOrGetLobby(lobbyId, user.value!!)
                lobbyRepository.joinLobby(
                    LobbyMember(
                        lobbyId = lobbyId,
                        userId = user.value!!.id,
                        user = user.value!!
                    )
                )
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error starting lobby flow", e)
            }
        }
    }

    fun toggleReady() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) {
                Log.w("LobbyViewModel", "toggleReady: User or Lobby is null")
                return@launch
            }
            val member = members.value?.find { it.userId == user.value!!.id }
            val newReady = !(member?.isReady ?: false)
            try {
                lobbyRepository.toggleReady(newReady)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error toggling ready", e)
            }
        }
    }

    fun leaveLobby() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) {
                Log.w("LobbyViewModel", "leaveLobby: User or Lobby is null")
                return@launch
            }
            try {
                val isCurrentUserHost = lobby.value?.hostId == user.value!!.id
                lobbyRepository.leaveLobby(lobby.value!!.id, user.value!!.id, isCurrentUserHost)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error leaving lobby", e)
            }
        }
    }

    fun startGame() {
        viewModelScope.launch {
            if (lobby.value == null) {
                Log.w("LobbyViewModel", "startGame: Lobby is null")
                return@launch
            }
            try {
                lobbyRepository.startGame(lobby.value!!.id)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error starting game", e)
                throw e
            }
        }
    }
}
