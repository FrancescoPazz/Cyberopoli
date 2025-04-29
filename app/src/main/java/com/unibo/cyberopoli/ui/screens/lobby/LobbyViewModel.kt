package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.auth.UserData
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
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

    private val _members = MutableStateFlow<List<LobbyMemberData>>(emptyList())
    val members: StateFlow<List<LobbyMemberData>> = _members.asStateFlow()

    fun startLobbyFlow(requestedId: String) = viewModelScope.launch {
        val userData = userRepository.currentUserLiveData.value ?: return@launch
        val me = UserData(
            id = userData.id,
            displayName = userData.displayName,
            isGuest = userData.isGuest
        )
        val createdId = lobbyRepo.createOrGetLobby(requestedId, me)
        _lobbyId.value = createdId
        lobbyRepo.joinLobby(
            createdId,
            LobbyMemberData(
                lobbyId     = createdId,
                userId      = me.id,
                isReady     = false,
                joinedAt    = Instant.now().toString(),
                displayName = me.displayName
            )
        )
        refreshMembers()
    }

    private fun refreshMembers() = viewModelScope.launch {
        _lobbyId.value?.let { id ->
            _members.value = lobbyRepo.fetchMembers(id)
        }
    }

    fun toggleReady() = viewModelScope.launch {
        val lobbyId = _lobbyId.value ?: return@launch
        val meId    = userRepository.currentUserLiveData.value?.id ?: return@launch
        val member  = _members.value.firstOrNull { it.userId == meId } ?: return@launch

        val newReady = !(member.isReady ?: false)
        lobbyRepo.toggleReady(lobbyId, meId, newReady)
        refreshMembers()
    }

    fun leaveLobby() = viewModelScope.launch {
        val id = _lobbyId.value ?: return@launch
        val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
        val isHost = _members.value.firstOrNull()?.userId == meId
        lobbyRepo.leaveLobby(id, meId, isHost)
        _lobbyId.value = null
        _members.value = emptyList()
    }

    fun startGame() = viewModelScope.launch {
        _lobbyId.value?.let { lobbyRepo.startGame(it) }
    }
}