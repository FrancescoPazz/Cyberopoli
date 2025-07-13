package com.unibo.cyberopoli.ui.screens.lobby.viewmodel

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateOf
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.models.lobby.LobbyResponse
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository

class LobbyViewModel(
    private val lobbyRepository: LobbyRepository
) : ViewModel() {
    val lobbyState: StateFlow<Lobby?> = lobbyRepository.currentLobby
    val membersState: StateFlow<List<LobbyMember>> = lobbyRepository.currentLobbyMembers

    private val _lobbyAlreadyStarted = mutableStateOf(false)
    val lobbyAlreadyStarted: State<Boolean> = _lobbyAlreadyStarted

    val allReady: Flow<Boolean> = membersState.map { currentMembers ->
        currentMembers.all { it.isReady }
    }

    fun isHost(user: User?): Boolean {
        return lobbyState.value?.hostId == user?.id
    }

    fun startLobbyFlow(lobbyId: String, user: User) {
        viewModelScope.launch {
            try {
                val response = lobbyRepository.createOrGetLobby(lobbyId, user)
                if (response is LobbyResponse.AlreadyStarted) {
                    _lobbyAlreadyStarted.value = true
                    return@launch
                }
                if (lobbyState.value == null) {
                    return@launch
                }
                lobbyRepository.joinLobby(
                    LobbyMember(
                        lobbyId = lobbyState.value?.id!!,
                        lobbyCreatedAt = lobbyState.value?.createdAt!!,
                        userId = user.id,
                        user = user,
                    ),
                )
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error starting lobby flow", e)
                throw e
            }
        }
    }

    fun toggleReady(user: User?) {
        viewModelScope.launch {
            if (user == null || lobbyState.value == null) {
                Log.w("LobbyViewModel", "toggleReady: User or Lobby is null")
                return@launch
            }
            val member = membersState.value.find { it.userId == user.id }
            val newReady = !(member?.isReady ?: false)
            try {
                lobbyRepository.toggleReady(newReady)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error toggling ready", e)
                throw e
            }
        }
    }

    fun setInApp(user: User?, inApp: Boolean) {
        viewModelScope.launch {
            if (user == null || lobbyState.value == null) {
                Log.w("LobbyViewModel", "setInApp: User or Lobby is null")
                return@launch
            }
            try {
                lobbyRepository.setInApp(inApp)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error setting in-app status", e)
                throw e
            }
        }
    }

    fun leaveLobby(user: User?) {
        viewModelScope.launch {
            if (user == null || lobbyState.value == null) {
                Log.w("LobbyViewModel", "leaveLobby: User or Lobby is null")
                return@launch
            }
            try {
                val isCurrentUserHost = lobbyState.value?.hostId == user.id
                lobbyRepository.leaveLobby(isCurrentUserHost)
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error leaving lobby", e)
                throw e
            }
        }
    }
}
