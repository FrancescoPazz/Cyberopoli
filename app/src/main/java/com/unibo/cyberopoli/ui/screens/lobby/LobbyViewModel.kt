package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMemberData
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {

    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby

    private val _players = MutableLiveData<List<LobbyMemberData>>()
    val players: LiveData<List<LobbyMemberData>> = _players

    private val _currentPlayer = MutableLiveData<LobbyMemberData?>()

    init {
        userRepository.loadUserData()
    }

    fun startLobbyFlow(lobbyId: String) {
        viewModelScope.launch {
            val me = userRepository.currentUserLiveData.value?.id ?: return@launch

            val lobbyObj = lobbyRepository.createOrGetLobby(lobbyId, me)
            _lobby.postValue(lobbyObj)

            val player = LobbyMemberData(
                lobbyId = lobbyId, userId = me, isReady = false,
                displayName = userRepository.currentUserLiveData.value?.displayName
            )
            val joined = lobbyRepository.joinLobby(player)
            _currentPlayer.postValue(joined)

            _players.postValue(lobbyRepository.fetchPlayers(lobbyId))
            _currentPlayer.postValue(
                lobbyRepository.fetchCurrentPlayer(lobbyId, me)
            )
        }
    }

    fun toggleReady() {
        viewModelScope.launch {
            val p = _currentPlayer.value ?: return@launch
            val updated = lobbyRepository.toggleReady(p)
            _currentPlayer.postValue(updated)
            _players.postValue(lobbyRepository.fetchPlayers(p.lobbyId!!))
        }
    }

    fun leaveLobby() {
        viewModelScope.launch {
            val p = _currentPlayer.value ?: return@launch
            lobbyRepository.leaveLobby(p.lobbyId!!, p.userId!!, lobby.value?.hostId!! == p.userId)
            _currentPlayer.postValue(null)
            _players.postValue(emptyList())
        }
    }

    fun startGame() {
        viewModelScope.launch {
            lobby.value?.lobbyId?.let {
                lobbyRepository.startGame(it)
                _lobby.postValue(lobby.value!!.copy(status = "in_progress"))
            }
        }
    }
}
