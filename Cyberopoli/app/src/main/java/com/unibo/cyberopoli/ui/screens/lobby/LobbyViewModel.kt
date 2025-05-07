package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private var _isHost = MutableLiveData<Boolean?>(null)
    val isHost: LiveData<Boolean?> = _isHost
    private var _allReady = MutableLiveData(false)
    val allReady: LiveData<Boolean?> = _allReady

    fun startLobbyFlow(lobbyId: String) {
        viewModelScope.launch {
            if (user.value == null) return@launch
            lobbyRepository.createOrGetLobby(lobbyId, user.value!!)
            lobbyRepository.joinLobby(
                LobbyMember(
                    lobbyId = lobby.value!!.id, userId = user.value!!.id, user = user.value!!
                )
            )
            _isHost.value = lobby.value?.hostId == user.value!!.id
            refreshMembers()
        }
    }

    private fun refreshMembers() {
        viewModelScope.launch {
            lobby.value?.id?.let { lobbyRepository.fetchMembers(it) }
        }
    }

    fun toggleReady() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) return@launch
            val member = members.value?.find { it.userId == user.value!!.id } ?: return@launch
            val newReady = !(member.isReady)
            lobbyRepository.toggleReady(lobby.value!!.id, user.value!!.id, newReady)
            _allReady.value = members.value?.all { it.isReady } ?: false
            refreshMembers()
        }
    }

    fun leaveLobby() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) return@launch
            lobbyRepository.leaveLobby(lobby.value!!.id, user.value!!.id, _isHost.value == true)
            _allReady.value = false
            refreshMembers()
        }
    }

    fun startGame() {
        viewModelScope.launch {
            lobby.value?.let { lobbyRepository.startGame(it.id) }
        }
    }
}