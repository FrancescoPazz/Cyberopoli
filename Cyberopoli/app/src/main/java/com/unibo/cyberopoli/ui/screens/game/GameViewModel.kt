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
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GameDialogData {
    data class Chance(val question: String, val options: List<String>) : GameDialogData()
    data class Hacker(val question: String, val options: List<String>) : GameDialogData()
}

class GameViewModel(
    private val userRepository: UserRepository, private val repo: GameRepository
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

    private val _dialog = MutableStateFlow<GameDialogData?>(null)
    val dialog = _dialog.asStateFlow()

    fun startGame(
        lobbyId: String, lobbyMembers: List<LobbyMember>
    ) {
        viewModelScope.launch {
            Log.d("GameViewModel", "Starting game with lobbyId=$lobbyId, members=$lobbyMembers")
            val newGame = repo.createGame(lobbyId, lobbyMembers)
            _game.value = newGame
            joinGame()
            refreshPlayers()
            refreshEvents()
            updateTurnIndex()
        }
    }

    private suspend fun joinGame() {
        val g = _game.value ?: return
        repo.joinGame(g, myUserId!!)
        refreshPlayers()
    }

    fun rollDice() {
        val roll = (1..6).random()
        _diceRoll.value = roll
        _phase.value = Phase.MOVE
    }

    fun movePlayer() {
        val g = _game.value ?: return
        val roll = _diceRoll.value ?: return

        viewModelScope.launch {
            val me = _players.value.first { it.userId == myUserId }
            val path = PERIMETER_PATH
            val startIdx = path.indexOf(me.cellPosition).coerceAtLeast(0)
            val nextIdx = (startIdx + roll) % path.size
            val newPos = path[nextIdx]

            repo.updatePlayer(g, me.copy(cellPosition = newPos))
            refreshPlayers()

            val cellType = PERIMETER_CELLS[newPos]?.type ?: CellType.COMMON
            _landedCellType.value = cellType

            _phase.value = when (cellType) {
                CellType.CHANCE -> Phase.CHANCE
                CellType.HACKER -> Phase.HACKER
                else -> Phase.END_TURN
            }

            handleLanded(cellType)
        }
    }

    private fun handleLanded(cellType: CellType) {
        when (cellType) {
            CellType.CHANCE -> _dialog.value = GameDialogData.Chance(
                question = "Qual è la capitale d'Italia?",
                options = listOf("Milano", "Roma", "Napoli")
            )

            CellType.HACKER -> _dialog.value = GameDialogData.Hacker(
                question = "Un hacker ti sfida: cosa fai?",
                options = listOf("Bloccalo", "Scappa", "Negozia")
            )

            else -> Unit
        }
    }

    fun onDialogOptionSelected(idx: Int, playerId: String) {
        when (val d = _dialog.value) {
            is GameDialogData.Chance -> {
                val delta = if (idx == 1) +5 else -5
                updatePlayerPoints(playerId, delta, GameEventType.CHANCE)
            }

            is GameDialogData.Hacker -> {
                // implementa logica Hacker
            }

            null -> return
        }
        _dialog.value = null
        endTurn()
    }

    fun onDialogDismiss() {
        _dialog.value = null
        endTurn()
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
        viewModelScope.launch {
            repo.setNextTurn(
                _game.value!!, _players.value[_currentTurnIndex.value].userId
            )
            refreshEvents()
            updateTurnIndex()
        }
    }

    private fun updateTurnIndex() {
        val idx = _players.value.indexOfFirst { it.userId == _game.value?.turn }
        _currentTurnIndex.value = idx
        _phase.value =
            if (_players.value.getOrNull(idx)?.userId == myUserId) Phase.ROLL_DICE else Phase.WAIT
    }

    fun updatePlayerPoints(userId: String, value: Int, gameEventType: GameEventType) {
        viewModelScope.launch {
            val g = _game.value ?: return@launch
            repo.addGameEvent(
                GameEvent(
                    lobbyId = g.lobbyId,
                    gameId = g.id,
                    senderUserId = myUserId!!,
                    eventType = gameEventType,
                    value = value,
                    recipientUserId = userId
                )
            )
            refreshPlayers()
            refreshEvents()
        }
    }

    private suspend fun refreshPlayers() {
        _players.value = _game.value?.id?.let { repo.getGamePlayers(it) }.orEmpty()
    }

    private suspend fun refreshEvents() {
        _events.value = _game.value?.let { repo.getGameEvents(it.lobbyId, it.id) }.orEmpty()
    }
}