package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.repositories.UserRepository


private const val LOBBIES = "lobbies"
private const val PLAYERS = "players"

class LobbyViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {


    init {
        loadUserData()
    }

    fun loadUserData() {
        userRepository.loadUserData()
    }

    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby


    fun observeLobby(lobbyId: String) {

    }

    fun joinLobby(lobbyId: String, playerName: String) {

    }

    fun toggleReady(lobbyId: String) {

    }

    fun leaveLobby(lobbyId: String) {

    }

    override fun onCleared() {
        super.onCleared()

    }

    fun startGame(lobbyId: String) {
    }
}
