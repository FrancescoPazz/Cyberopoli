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
    private val userRepository: UserRepository,
    private val lobbyRepository: LobbyRepository
) : ViewModel() {
    private val currentPlayer: LiveData<PlayerData?> = lobbyRepository.currentPlayerLiveData
    val players: LiveData<List<PlayerData>?> = lobbyRepository.playersLiveData

    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby

    init {
        userRepository.loadUserData()
    }

    fun observeLobby(lobbyId: String) {
        viewModelScope.launch {
            val existing = lobbyRepository.getLobby(lobbyId)
            if (existing != null) {
                _lobby.postValue(existing)
            } else {
                val me = userRepository.currentUserLiveData.value ?: return@launch
                val created = lobbyRepository.createLobby(
                    lobbyId = lobbyId,
                    hostId = me.id!!,
                )
                _lobby.postValue(created)
            }

            val meId = userRepository.currentUserLiveData.value?.id ?: return@launch
            lobbyRepository.fetchCurrentPlayer(lobbyId, meId)
        }
    }

    fun joinLobby(lobbyId: String) {
        viewModelScope.launch {
            val me = userRepository.currentUserLiveData.value ?: return@launch
            val player = PlayerData(
                lobbyId = lobbyId,
                userId  = me.id!!,
                isReady = false
            )
            lobbyRepository.joinLobby(player)
            lobbyRepository.getLobbyPlayers(lobbyId)
            observeLobby(lobbyId)
        }
    }

    fun toggleReady(lobbyId: String) {
        viewModelScope.launch {
            val player = currentPlayer.value ?: return@launch
            lobbyRepository.toggleReady(player.userId!!, player.isReady!!)
            observeLobby(lobbyId)
        }
    }

    fun leaveLobby() {
        viewModelScope.launch {
            val me = currentPlayer.value ?: return@launch
            lobbyRepository.leaveLobby(me.userId!!)
            _lobby.postValue(null)
            lobbyRepository.currentPlayerLiveData.postValue(null)
        }
    }

    fun startGame(lobbyId: String) {
        viewModelScope.launch {
            lobbyRepository.startGame(lobbyId)
            observeLobby(lobbyId)
        }
    }
}
