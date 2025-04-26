package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.PlayerData
import com.unibo.cyberopoli.data.repositories.LobbyRepository
import com.unibo.cyberopoli.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val userRepository: UserRepository, private val lobbyRepository: LobbyRepository
) : ViewModel() {

    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby

    private val _players = MutableLiveData<List<PlayerData>>()
    val players: LiveData<List<PlayerData>> = _players

    private val _currentPlayer = MutableLiveData<PlayerData?>()

    init {
        userRepository.loadUserData()
    }

    fun startLobbyFlow(lobbyId: String) {
        viewModelScope.launch {
            val me = userRepository.currentUserLiveData.value?.id ?: return@launch

            val lobbyObj = lobbyRepository.createOrGetLobby(lobbyId, me)
            _lobby.postValue(lobbyObj)

            val player = PlayerData(
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
            val l = _lobby.value ?: return@launch
            lobbyRepository.startGame(l.lobbyId!!)
            _lobby.postValue(l.copy(status = "started"))
        }
    }
}
