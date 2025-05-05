package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.game.CellType
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GameEventType
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.PERIMETER_CELLS
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
    private val myUserId: String?
        get() = userRepository.currentUserLiveData.value?.id

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

    private val _landedCellType = MutableStateFlow<CellType>(CellType.START)
    val landedCellType = _landedCellType.asStateFlow()

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

    private suspend fun joinGame() {
        val g = _game.value ?: return
        repo.joinGame(g, myUserId!!)
        Log.d("GameViewModel", "joinGame(): joined as player to ${g.id}")
        refreshPlayers()
    }

    fun rollDice() {
        val roll = (1..6).random()
        _diceRoll.value = roll
        _phase.value = Phase.MOVE
    }

    private fun perimeterPath(rows: Int, cols: Int): List<Int> {
        val path = mutableListOf<Int>()
        for (c in 0 until cols)        path += c
        for (r in 1 until rows - 1)    path += r * cols + (cols - 1)
        for (c in cols - 1 downTo 0)    path += (rows - 1) * cols + c
        for (r in rows - 2 downTo 1)    path += r * cols
        return path
    }

    fun movePlayer() {
        val g    = _game.value ?: return
        val roll = _diceRoll.value ?: return

        viewModelScope.launch {
            val me       = _players.value.first { it.userId == myUserId }
            val path     = perimeterPath(rows = 5, cols = 5)
            val startIdx = path.indexOf(me.cellPosition).takeIf { it >= 0 } ?: 0
            val nextIdx  = (startIdx + roll) % path.size
            val newPos   = path[nextIdx]

            val updated = repo.updatePlayer(g, me.copy(cellPosition = newPos))
            if (updated == null) {
                Log.e("GameViewModel", "movePlayer: updatePlayer è tornato null!")
            } else {
                Log.d("GameViewModel", "movePlayer: successo, nuova pos = ${updated.cellPosition}")
            }
            refreshPlayers()

            val cellType = PERIMETER_CELLS[newPos]?.type
                ?: CellType.COMMON
            _landedCellType.value = cellType

            Log.d("GameViewModel", "Landed on cell type: $cellType")

            _phase.value = when (cellType) {
                CellType.CHANCE  -> Phase.CHANCE
                CellType.HACKER  -> Phase.HACKER
                else             -> Phase.END_TURN
            }
        }
    }



    fun performChance() {
        _phase.value = Phase.END_TURN
    }

    fun performHacker() {
        _phase.value = Phase.END_TURN
    }

    fun endTurn() {
        _phase.value = Phase.WAIT
        _diceRoll.value = null
        nextTurn()
    }

    private fun nextTurn() {
        val count = _players.value.size
        if (count == 0) return
        _currentTurnIndex.value = (_currentTurnIndex.value + 1) % count
        Log.d("GameViewModel", "Next turn: current index = ${_currentTurnIndex.value}, players count = $count")

        val g = _game.value ?: return
        viewModelScope.launch {
            val nextPlayer = _players.value[_currentTurnIndex.value]
            repo.setNextTurn(g, nextPlayer.userId)
            refreshEvents()
            updateTurnIndex()
        }
    }

    fun updatePlayerPoints(userId: String, value: Int, gameEventType: GameEventType) {
        val g = _game.value ?: return
        viewModelScope.launch {
            val evt = GameEvent(
                lobbyId         = g.lobbyId,
                gameId          = g.id,
                senderUserId    = myUserId!!,
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
        val idx = _players.value.indexOfFirst { it.userId == _game.value?.turn }
        _currentTurnIndex.value = idx

        _phase.value = if (_players.value.getOrNull(idx)?.userId == myUserId) {
            Phase.ROLL_DICE
        } else {
            Phase.WAIT
        }

        Log.d("GameViewModel", "Current turn index: $idx, phase = ${_phase.value}")
    }

}