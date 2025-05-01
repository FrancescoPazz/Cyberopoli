package com.unibo.cyberopoli.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game = _game.asStateFlow()

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players = _players.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex = _currentTurnIndex.asStateFlow()

    fun startGame(lobbyId: String) {
        viewModelScope.launch {
            val newMatch = gameRepository.createGame(lobbyId)
            _game.value = newMatch
            _players.value = gameRepository.getGamePlayers(newMatch.id)
        }
    }

    fun nextTurn() {
        _currentTurnIndex.value = (_currentTurnIndex.value + 1) % _players.value.size
    }

    fun updatePlayerPoints(userId: String, delta: Int) {
        viewModelScope.launch {
            gameRepository.addPointEvent(matchId = _game.value!!.id, userId = userId, delta = delta)
            _players.value = gameRepository.getGamePlayers(_game.value!!.id)
        }
    }
}
