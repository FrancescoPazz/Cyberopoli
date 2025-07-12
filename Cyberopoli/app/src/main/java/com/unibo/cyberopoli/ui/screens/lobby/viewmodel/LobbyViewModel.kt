package com.unibo.cyberopoli.ui.screens.lobby.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyResponse
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val lobbyRepository: LobbyRepository
) : ViewModel() {
    val lobby: LiveData<Lobby?> = lobbyRepository.currentLobbyLiveData
    val members: LiveData<List<LobbyMember>?> = lobbyRepository.currentMembersLiveData

    private val _lobbyAlreadyStarted = mutableStateOf(false)
    val lobbyAlreadyStarted: State<Boolean> = _lobbyAlreadyStarted

    val allReady: LiveData<Boolean> = members.map { currentMembers ->
        currentMembers?.all { it.isReady } ?: false
    }

    fun isHost(user: User?): Boolean {
        return lobby.value?.hostId == user?.id
    }

    fun startLobbyFlow(lobbyId: String, user: User) {
        viewModelScope.launch {
            try {
                val response = lobbyRepository.createOrGetLobby(lobbyId, user)
                if (response is LobbyResponse.AlreadyStarted) {
                    _lobbyAlreadyStarted.value = true
                    return@launch
                }
                if (lobby.value == null) {
                    return@launch
                }
                lobbyRepository.joinLobby(
                    LobbyMember(
                        lobbyId = lobby.value?.id!!,
                        lobbyCreatedAt = lobby.value?.createdAt!!,
                        userId = user.id,
                        user = user,
                    ),
                )
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error starting lobby flow", e)
            }
        }
    }

    fun toggleReady(user: User?) {
        viewModelScope.launch {
            if (user == null || lobby.value == null) {
                Log.w("LobbyViewModel", "toggleReady: User or Lobby is null")
                return@launch
            }
            val member = members.value?.find { it.userId == user.id }
            val newReady = !(member?.isReady ?: false)
            try {
                lobbyRepository.toggleReady(newReady)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error toggling ready", e)
            }
        }
    }

    fun setInApp(user: User?, inApp: Boolean) {
        viewModelScope.launch {
            if (user == null || lobby.value == null) {
                Log.w("LobbyViewModel", "setInApp: User or Lobby is null")
                return@launch
            }
            try {
                lobbyRepository.setInApp(inApp)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error setting in-app status", e)
            }
        }
    }

    fun leaveLobby(user: User?) {
        viewModelScope.launch {
            if (user == null || lobby.value == null) {
                Log.w("LobbyViewModel", "leaveLobby: User or Lobby is null")
                return@launch
            }
            try {
                val isCurrentUserHost = lobby.value?.hostId == user.id
                lobbyRepository.leaveLobby(isCurrentUserHost)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error leaving lobby", e)
            }
        }
    }
}
