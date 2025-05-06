package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {

    private val _lobbyId = MutableStateFlow<String?>(null)
    val lobbyId: StateFlow<String?> = _lobbyId.asStateFlow()

    private val _members = MutableStateFlow<List<LobbyMember>>(emptyList())
    val members: StateFlow<List<LobbyMember>> = _members.asStateFlow()

    fun startLobbyFlow(lobbyId: String) = viewModelScope.launch {
        val userData = userRepository.currentUserLiveData.value ?: return@launch
        val me = User(
            id = userData.id, username = userData.username, isGuest = userData.isGuest
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
        val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
        val member = _members.value.firstOrNull { it.userId == meId } ?: return@launch

        val newReady = !(member.isReady)
        lobbyRepository.toggleReady(lobbyId, meId, newReady)
        refreshMembers()
    }

    fun leaveLobby() = viewModelScope.launch {
        val id = _lobbyId.value ?: return@launch
        val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
        val isHost = _members.value.firstOrNull()?.userId == meId
        lobbyRepository.leaveLobby(id, meId, isHost)
        _lobbyId.value = null
        _members.value = emptyList()
    }

    fun startGame() = viewModelScope.launch {
        _lobbyId.value?.let { lobbyRepository.startGame(it) }
    }
}