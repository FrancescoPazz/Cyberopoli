package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameEventType
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
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

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex = _currentTurnIndex.asStateFlow()

    fun startGame(
        lobbyId: String,
        lobbyMembers: List<LobbyMember>
    ) {
        viewModelScope.launch {
            Log.d("GameViewModel", "Starting game with lobbyId=$lobbyId")
            val newGame = gameRepository.createGame(lobbyId, lobbyMembers)
            _game.value = newGame

            _players.value = gameRepository.getGamePlayers(newGame.id)

            _events.value = gameRepository.getGameEvents(lobbyId = newGame.lobbyId, gameId = newGame.id)

            _currentTurnIndex.value = _players.value.indexOfFirst { it.userId == newGame.turn }
        }
    }

    fun nextTurn() {
        val count = _players.value.size
        if (count == 0) return

        _currentTurnIndex.value = (_currentTurnIndex.value + 1) % count

        val g = _game.value ?: return
        viewModelScope.launch {
            val nextPlayer = _players.value[_currentTurnIndex.value]
            val updatedGame = gameRepository.setNextTurn(g, nextPlayer.userId)
        }
    }

    fun updatePlayerPoints(userId: String, value: Int, gameEvent: GameEventType) {
        val g = _game.value ?: return
        viewModelScope.launch {
            val evt = GameEvent(
                lobbyId = g.lobbyId,
                gameId = g.id,
                senderUserId = userId,
                eventType = GameEventType.CHANCE,
                value = value,
            )
            gameRepository.addGameEvent(evt)

            _players.value = gameRepository.getGamePlayers(g.id)
            _events.value = gameRepository.getGameEvents(g.lobbyId, g.id)
        }
    }
}
