package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {
    private val user = userRepository.currentUserLiveData.value
        ?: throw IllegalStateException("User not logged in")

    private val _lobbyId = MutableStateFlow<String?>(null)
    val lobbyId: StateFlow<String?> = _lobbyId.asStateFlow()

    private val _members = MutableStateFlow<List<LobbyMember>>(emptyList())
    val members: StateFlow<List<LobbyMember>> = _members.asStateFlow()

    fun startLobbyFlow(lobbyId: String) = viewModelScope.launch {
        val me = User(
            id = user.id, username = user.username, isGuest = user.isGuest
        )
        val createdId = lobbyRepository.createOrGetLobby(lobbyId, me)
        _lobbyId.value = createdId
        lobbyRepository.joinLobby(
            createdId, LobbyMember(
                lobbyId = createdId, userId = me.id, user = me
            )
        )
        refreshMembers()
    }

    private fun refreshMembers() = viewModelScope.launch {
        _lobbyId.value?.let { id ->
            _members.value = lobbyRepository.fetchMembers(id)
        }
    }

    fun toggleReady() = viewModelScope.launch {
        val lobbyId = _lobbyId.value ?: return@launch
        val member = _members.value.firstOrNull { it.userId == user.id } ?: return@launch

        val newReady = !(member.isReady)
        lobbyRepository.toggleReady(lobbyId, user.id, newReady)
        refreshMembers()
    }

    fun leaveLobby() = viewModelScope.launch {
        val id = _lobbyId.value ?: return@launch
        val isHost = _members.value.firstOrNull()?.userId == user.id
        lobbyRepository.leaveLobby(id, user.id, isHost)
        _lobbyId.value = null
        _members.value = emptyList()
    }

    fun startGame() = viewModelScope.launch {
        _lobbyId.value?.let { lobbyRepository.startGame(it) }
    }
}