package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.game.GameTypeCell
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.PERIMETER_CELLS
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.data.models.game.GameState
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {
    val game: LiveData<Game?> = gameRepository.currentGameLiveData
    val player: LiveData<GamePlayer?> = gameRepository.currentPlayerLiveData

    val gameState: MutableLiveData<GameState?> = MutableLiveData(GameState.ROLL_DICE)

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players: StateFlow<List<GamePlayer>> = _players.asStateFlow()

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())

    private val _diceRoll = MutableStateFlow<Int?>(null)
    val diceRoll: StateFlow<Int?> = _diceRoll.asStateFlow()

    private val _dialog = MutableStateFlow<GameDialogData?>(null)
    val dialog: StateFlow<GameDialogData?> = _dialog.asStateFlow()

    private val _isLoadingQuestion = MutableStateFlow(false)
    val isLoadingQuestion: StateFlow<Boolean> = _isLoadingQuestion.asStateFlow()

    private val _skipNext = MutableStateFlow(false)
    val skipNext: StateFlow<Boolean> = _skipNext.asStateFlow()

    private val _hasVpn = MutableStateFlow(false)
    val hasVpn: StateFlow<Boolean> = _hasVpn.asStateFlow()

    private val _blocks = MutableStateFlow<Set<GamePlayer>>(emptySet())
    val blocks: StateFlow<Set<GamePlayer>> = _blocks.asStateFlow()

    private fun nextTurn() {
        if (game.value == null) return
        viewModelScope.launch {
            _players.value.let { players ->
                val nextIdx = (players.indexOfFirst { it.userId == game.value!!.turn } + 1) % players.size
                gameRepository.setNextTurn(players[nextIdx].userId) // TODO: Avvisare tutti con realtime
                if (players[nextIdx].userId == player.value!!.userId) {
                    if (_skipNext.value) {
                        _skipNext.value = false
                        nextTurn()
                    } else {
                        gameState.value = GameState.ROLL_DICE
                    }
                }
            }
        }
    }

    private fun computeNewPosition(current: Int, roll: Int): Int {
        val path = PERIMETER_PATH
        val idx = (path.indexOf(current).coerceAtLeast(0) + roll) % path.size
        return path[idx]
    }

    private fun handleLanding(gameTypeCell: GameTypeCell?) {
        viewModelScope.launch {
            val me = player.value ?: return@launch
            when (gameTypeCell) {
                GameTypeCell.START -> {
                    updatePlayerPoints(+50)
                }
                GameTypeCell.CHANCE -> {
                    askQuestion(GameTypeCell.CHANCE)
                }
                GameTypeCell.HACKER -> {
                    askQuestion(GameTypeCell.HACKER)
                }
                else -> {}
            }
        }
    }

    private fun askQuestion(eventType: GameTypeCell) {
        _isLoadingQuestion.value = true
        when (eventType) {
            GameTypeCell.CHANCE -> {
                val questions = gameRepository.chanceQuestions.value.orEmpty()
                if (questions.isEmpty()) {
                    throw IllegalStateException("No questions available for event type: $eventType")
                }
                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                gameRepository.chanceQuestions.postValue(updatedQuestions)
                _dialog.value = question
                gameState.value = GameState.CHANCE
            }
            GameTypeCell.HACKER -> {
                val questions = gameRepository.hackerStatements.value.orEmpty()
                if (questions.isEmpty()) {
                    throw IllegalStateException("No questions available for event type: $eventType")
                }
                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                gameRepository.hackerStatements.postValue(updatedQuestions)
                _dialog.value = question
                updatePlayerPoints(-question.points)
                gameState.value = GameState.HACKER
            }
            GameTypeCell.BLOCK -> {
                // TODO CONTINUARE DA QUI
            }
            else -> {
                throw IllegalStateException("Invalid event type: $eventType")
            }
        }
        _isLoadingQuestion.value = false
    }

    fun startGame(lobbyId: String, lobbyMembers: List<LobbyMember>) {
        viewModelScope.launch {
            gameRepository.createGame(lobbyId, lobbyMembers)
            joinGame()
        }
    }

    private fun joinGame() {
        viewModelScope.launch {
            gameRepository.joinGame()
            refreshPlayers()
        }
    }

    private fun refreshPlayers() {
        viewModelScope.launch {
            _players.value = gameRepository.getGamePlayers()
        }
    }

    private fun refreshEvents() {
        viewModelScope.launch {
            _events.value = gameRepository.getGameEvents()
        }
    }

    fun rollDice() {
        _diceRoll.value = (1..6).random()
        gameState.value = GameState.MOVE
    }

    fun movePlayer() {
        viewModelScope.launch {
            if (game.value == null || player.value == null) return@launch
            _players.value.firstOrNull { it.userId == player.value!!.userId }?.let { me ->
                val newPos = computeNewPosition(me.cellPosition, _diceRoll.value ?: 0)
                Log.d("GameViewModel", "New position: $newPos")
                gameRepository.updatePlayerPosition(newPos)
                _players.value = _players.value.map {
                    if (it.userId == me.userId) it.copy(cellPosition = newPos) else it
                }
                handleLanding(PERIMETER_CELLS[newPos]?.type)
            }
            gameState.value = GameState.END_TURN
        }
    }

    fun updatePlayerPoints(value: Int) {
        viewModelScope.launch {
            gameRepository.updatePlayerPoints(value)
            val updated = player.value?.copy(
                score = (player.value?.score ?: 0) + value
            )
            if (updated != null) {
                gameRepository.currentPlayerLiveData.postValue(updated)
                _players.value = _players.value.map {
                    if (it.userId == updated.userId) updated else it
                }
            }
        }
    }

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            (dialog.value as? GameDialogData.ChanceQuestion)?.let { q ->
                val correct = (idx == q.correctIndex)
                val delta = if (correct) q.points else -q.points
                updatePlayerPoints(delta)
                val title = if (correct) "Corretto!" else "Sbagliato!"
                val message = if (correct)
                    "Hai guadagnato ${q.points} punti."
                else
                    "Hai perso ${q.points} punti."
                _dialog.value = GameDialogData.Alert(title, message)
            }
        }
    }

    fun onResultDismiss() {
        _dialog.value = null
        endTurn()
    }

    fun endTurn() {
        gameState.value = GameState.WAIT
        _diceRoll.value = null
        nextTurn()
    }
}
