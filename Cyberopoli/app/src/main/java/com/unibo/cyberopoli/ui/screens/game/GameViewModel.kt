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
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val userRepository: UserRepository,
    private val repo: GameRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game = _game.asStateFlow()

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players = _players.asStateFlow()

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex = _currentTurnIndex.asStateFlow()

    private val _phase = MutableStateFlow(Phase.ROLL_DICE)
    val phase = _phase.asStateFlow()

    private val _diceRoll = MutableStateFlow<Int?>(null)
    val diceRoll = _diceRoll.asStateFlow()

    fun startGame(
        lobbyId: String,
        lobbyMembers: List<LobbyMember>
    ) {
        viewModelScope.launch {
            Log.d("GameViewModel", "Starting game with lobbyId=$lobbyId, members=$lobbyMembers")
            val newGame = repo.createGame(lobbyId, lobbyMembers)
            _game.value = newGame
            Log.d("GameViewModel", "Game created: $newGame")

            joinGame()

            refreshPlayers()
            refreshEvents()
            updateTurnIndex()
        }
    }

    private fun joinGame() {
        val userData = userRepository.currentUserLiveData.value
        val g = _game.value ?: return
        viewModelScope.launch {
            repo.joinGame(g, userData?.id!!)
            Log.d("GameViewModel", "joinGame(): joined as player to ${g.id}")
            refreshPlayers()
        }
    }

    fun rollDice() {
        val roll = (1..6).random()
        _diceRoll.value = roll
        _phase.value = Phase.MOVE
    }

    fun movePlayer() {
        val g = _game.value ?: return
        viewModelScope.launch {
            val myPlayer = _players.value
                .firstOrNull { it.userId == userRepository.currentUserLiveData.value?.id }
                ?: return@launch
            val updatedPlayer = myPlayer.copy(cellPosition = myPlayer.cellPosition + _diceRoll.value!!)
            repo.updatePlayer(g, updatedPlayer)
            refreshPlayers()
            _phase.value = Phase.END_TURN
        }
    }

    fun performChance() {
        _phase.value = Phase.END_TURN
    }

    fun performHacker() {
        _phase.value = Phase.END_TURN
    }

    fun endTurn() {
        nextTurn()
        _diceRoll.value = null
        _phase.value = Phase.WAIT
    }

    private fun nextTurn() {
        val count = _players.value.size
        if (count == 0) return
        _currentTurnIndex.value = (_currentTurnIndex.value + 1) % count

        val g = _game.value ?: return
        viewModelScope.launch {
            val nextPlayer = _players.value[_currentTurnIndex.value]
            repo.setNextTurn(g, nextPlayer.userId)
            refreshEvents()
            updateTurnIndex()
        }
    }

    fun updatePlayerPoints(userId: String?, value: Int, gameEventType: GameEventType) {
        val g = _game.value ?: return
        viewModelScope.launch {
            val evt = GameEvent(
                lobbyId         = g.lobbyId,
                gameId          = g.id,
                senderUserId    = userRepository.currentUserLiveData.value?.id!!,
                eventType       = gameEventType,
                value           = value,
                recipientUserId = userId,
            )
            repo.addGameEvent(evt)
            refreshPlayers()
            refreshEvents()
        }
    }

    private suspend fun refreshPlayers() {
        _players.value = _game.value
            ?.let { repo.getGamePlayers(it.id) }
            .orEmpty()
        Log.d("GameViewModel", "Players refreshed: ${_players.value}")
    }

    private suspend fun refreshEvents() {
        _events.value = _game.value
            ?.let { repo.getGameEvents(it.lobbyId, it.id) }
            .orEmpty()
        Log.d("GameViewModel", "Events refreshed: ${_events.value}")
    }

    private fun updateTurnIndex() {
        _currentTurnIndex.value = _players.value.indexOfFirst { it.userId == _game.value?.turn }
        Log.d("GameViewModel", "Current turn index: ${_currentTurnIndex.value}")
    }
}