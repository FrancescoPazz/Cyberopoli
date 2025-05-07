package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {
    val user: LiveData<User?> = userRepository.currentUserLiveData
    val lobby: LiveData<Lobby?> = lobbyRepository.currentLobbyLiveData

    private val _members = MutableStateFlow<List<LobbyMember>>(emptyList())
    val members: StateFlow<List<LobbyMember>> = _members.asStateFlow()

    fun startLobbyFlow(lobbyId: String) {
        viewModelScope.launch {
            if (user.value == null) return@launch
            lobbyRepository.createOrGetLobby(lobbyId, user.value!!)
            lobbyRepository.joinLobby(
                LobbyMember(
                    lobbyId = lobby.value!!.id, userId = user.value!!.id, user = user.value!!
                )
            )
            refreshMembers()
        }
    }

    private fun refreshMembers() {
        viewModelScope.launch {
            lobby.value?.id?.let { _members.value = lobbyRepository.fetchMembers(it) }
        }
    }

    fun toggleReady() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) return@launch
            val member = _members.value.firstOrNull { it.userId == user.value!!.id } ?: return@launch
            val newReady = !(member.isReady)
            lobbyRepository.toggleReady(lobby.value!!.id, user.value!!.id, newReady)
            refreshMembers()
        }
    }

    fun leaveLobby() {
        viewModelScope.launch {
            if (user.value == null || lobby.value == null) return@launch
            val isHost = _members.value.firstOrNull()?.userId == user.value!!.id
            lobbyRepository.leaveLobby(lobby.value!!.id, user.value!!.id, isHost)
            _members.value = emptyList()
        }
    }

    fun startGame() = viewModelScope.launch {
        lobby.value?.let { lobbyRepository.startGame(it.id) }
    }
}