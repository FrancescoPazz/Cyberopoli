package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.domain.model.LobbyMember
import com.unibo.cyberopoli.domain.model.User
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class LobbyViewModel(
    private val userRepository: UserRepository,
    private val lobbyRepo: LobbyRepository
) : ViewModel() {

    private val _lobbyId = MutableStateFlow<String?>(null)
    val lobbyId: StateFlow<String?> = _lobbyId.asStateFlow()

    private val _members = MutableStateFlow<List<LobbyMember>>(emptyList())
    val members: StateFlow<List<LobbyMember>> = _members.asStateFlow()

    fun startLobbyFlow(requestedId: String) = viewModelScope.launch {
        val userData = userRepository.currentUserLiveData.value ?: return@launch
        val me = User(
            id = userData.id,
            displayName = userData.displayName,
            isGuest = userData.isGuest
        )
        val createdId = lobbyRepo.createOrGetLobby(requestedId, me)
        _lobbyId.value = createdId
        lobbyRepo.joinLobby(createdId, LobbyMember(me, isReady = false, joinedAt = Instant.now()))
        refreshMembers()
    }

    private fun refreshMembers() = viewModelScope.launch {
        _lobbyId.value?.let { id ->
            _members.value = lobbyRepo.fetchMembers(id)
        }
    }

    fun toggleReady() = viewModelScope.launch {
        val id = _lobbyId.value ?: return@launch
        val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
        val member = _members.value.firstOrNull { it.user.id == meId } ?: return@launch
        lobbyRepo.toggleReady(id, meId, !member.isReady)
        refreshMembers()
    }


    fun leaveLobby() = viewModelScope.launch {
        val id = _lobbyId.value ?: return@launch
        val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
        val isHost = _members.value.firstOrNull()?.user?.id == meId
        lobbyRepo.leaveLobby(id, meId, isHost)
        _lobbyId.value = null
        _members.value = emptyList()
    }

    fun startGame() = viewModelScope.launch {
        _lobbyId.value?.let { lobbyRepo.startGame(it) }
    }
}