package com.unibo.cyberopoli.ui.screens.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.game.GameEventType
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

    private var pendingGameEvent: GameEvent? = null

    private fun nextTurn() {
        if (game.value == null) return
        viewModelScope.launch {
            _players.value.let { players ->
                val nextIdx = (players.indexOfFirst { it.userId == game.value!!.turn } + 1) % players.size
                gameRepository.setNextTurn(players[nextIdx].userId) // TODO: Avvisare tutti con realtime
                if (players[nextIdx].userId == player.value!!.userId) {
                    gameState.value = GameState.ROLL_DICE
                }
            }
        }
    }

    private fun computeNewPosition(current: Int, roll: Int): Int {
        val path = PERIMETER_PATH
        val idx = (path.indexOf(current).coerceAtLeast(0) + roll) % path.size
        return path[idx]
    }

    private fun handleLanding(gameEventType: GameEventType?) {
        Log.d("TEST", "handleLanding: $gameEventType")
        viewModelScope.launch {
            when (gameEventType) {
                GameEventType.START -> {
                    gameRepository.updatePlayerPoints(50)
                }
                GameEventType.CHANCE -> askQuestion(
                    eventType = GameEventType.CHANCE
                )
                GameEventType.HACKER -> askQuestion(
                    eventType = GameEventType.HACKER
                )
                else -> {

                }
            }
        }
    }

    private fun askQuestion(eventType: GameEventType) {
        Log.d("TEST", "askQuestion: $eventType")

        when (eventType) {
            GameEventType.CHANCE -> {
                val questions = gameRepository.chanceQuestions.value.orEmpty()
                Log.d("TEST", "askQuestion: ${questions.size} questions available")

                val question = questions.firstOrNull()
                    ?: throw IllegalStateException("No questions available for event type: $eventType")

                gameRepository.chanceQuestions.postValue(questions.drop(1))
                _dialog.value = question
            }
            GameEventType.HACKER -> {
                val questions = gameRepository.hackerStatements.value.orEmpty()
                Log.d("TEST", "askQuestion: ${questions.size} questions available")

                val question = questions.firstOrNull()
                    ?: throw IllegalStateException("No questions available for event type: $eventType")

                gameRepository.hackerStatements.postValue(questions.drop(1))
                _dialog.value = question
            }
            else -> {
                throw IllegalStateException("Invalid event type: $eventType")
            }
        }
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

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            (dialog.value as? GameDialogData.ChanceQuestion)?.let { q ->
                val correct = (idx == q.correctIndex)
                val delta = if (correct) q.correctIndex else -q.correctIndex
                pendingGameEvent?.copy(value = delta)?.also { gameRepository.addGameEvent(it) }
                val title = if (correct) "Corretto!" else "Sbagliato!"
                val message =
                    if (correct) "Hai guadagnato $delta punti." else "Hai perso ${-delta} punti."
                _dialog.value = GameDialogData.Result(title, message)
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
