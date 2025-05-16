package com.unibo.cyberopoli.ui.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameTypeCell
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.PERIMETER_CELLS
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.data.models.game.GameAction
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {
    val game: LiveData<Game?> = gameRepository.currentGameLiveData
    private val player: LiveData<GamePlayer?> = gameRepository.currentPlayerLiveData

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

    private val _actionsPermitted = MutableStateFlow<List<GameAction>>(listOf(
        GameAction( // TODO: da cambiare
            id = "roll_dice",
            iconRes = R.drawable.ic_dice,
            action = {
                rollDice()
            },
        ),
    ))
    val actionsPermitted: StateFlow<List<GameAction>> = _actionsPermitted.asStateFlow()

    private fun nextTurn() {
        if (game.value == null) return
        viewModelScope.launch {
            _players.value.let { players ->
                val nextIdx = (players.indexOfFirst { it.userId == game.value!!.turn } + 1) % players.size
                gameRepository.setNextTurn(players[nextIdx].userId)
                if (players[nextIdx].userId == player.value!!.userId) {
                    if (_skipNext.value) {
                        _skipNext.value = false
                        nextTurn()
                    } else {
                        _actionsPermitted.value = listOf(
                            GameAction(
                                id = "roll_dice",
                                iconRes = R.drawable.ic_dice,
                                action = {
                                    rollDice()
                                },
                            ),
                        )
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
        viewModelScope.launch {
            _diceRoll.value = (1..6).random()
            _dialog.value = GameDialogData.Alert(
                title = "Tiro il dado",
                message = "Hai tirato un dado e hai ottenuto ${_diceRoll.value}",
            )
            _actionsPermitted.value = listOf(
                GameAction(
                    id = "move_on",
                    iconRes = R.drawable.ic_move_on,
                    action = {
                        movePlayer()
                    },
                ),
            )
        }
    }

    private fun handleLanding(gameCell: GameCell) {
        val gameTypeCell = gameCell.type

        viewModelScope.launch {
            _actionsPermitted.value = listOf(
                GameAction(
                    id = "turn_pass",
                    iconRes = R.drawable.ic_skip,
                    action = {
                        endTurn()
                    },
                ),
            )
            when (gameTypeCell) {
                GameTypeCell.START -> {
                    increasePlayerRound()
                }
                GameTypeCell.CHANCE -> {
                    askQuestion(GameTypeCell.CHANCE)
                }
                GameTypeCell.HACKER -> {
                    askQuestion(GameTypeCell.HACKER)
                }
                GameTypeCell.BLOCK -> {
                    askQuestion(GameTypeCell.BLOCK)
                }
                GameTypeCell.VPN -> {
                    _hasVpn.value = true
                    askQuestion(GameTypeCell.VPN)
                }
                GameTypeCell.BROKEN_ROUTER -> {
                    // TODO
                }
                else -> {
                    if (gameCell.contentOwner.isNullOrEmpty()) {
                        _actionsPermitted.value += listOf(GameAction(
                            id = "subscribe_cell",
                            iconRes = R.drawable.ic_subscribe,
                            action = {
                                updatePlayerPoints(-gameCell.value!!)
                            },
                        ))
                    } else if (gameCell.contentOwner == player.value?.userId){
                        _actionsPermitted.value += listOf(GameAction(
                            id = "make_content",
                            iconRes = R.drawable.ic_made_content,
                            action = {
                                gameCell.contentOwner = player.value?.userId
                                updatePlayerPoints(-gameCell.value!!)
                            },
                        ))
                    } else {
                        gameCell.value?.let { updatePlayerPoints(-it) }
                    }
                }
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
                updatePlayerPoints(question.cost)
            }
            GameTypeCell.BLOCK -> {
                val others = _players.value.filter { it.userId != player.value?.userId }
                _dialog.value = GameDialogData.BlockChoice(players = others)
            }
            GameTypeCell.VPN -> {
                _dialog.value = GameDialogData.Alert(
                    title = "VPN",
                    message = "Hai trovato una VPN! Puoi usarla per evitare di essere bloccato per un turno intero.",
                )
            }
            else -> {
                throw IllegalStateException("Invalid event type: $eventType")
            }
        }
        _isLoadingQuestion.value = false
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
                handleLanding(PERIMETER_CELLS[newPos]!!)
            }
        }
    }

    private fun increasePlayerRound() {
        viewModelScope.launch {
            gameRepository.increasePlayerRound()
            updatePlayerPoints(+50)
        }
    }

    fun updatePlayerPoints(value: Int) {
        viewModelScope.launch {
            gameRepository.updatePlayerPoints(value)
            val updated = player.value?.copy(
                score = (player.value?.score ?: 0) + value
            )
            if (updated != null) {
                _players.value = _players.value.map {
                    if (it.userId == updated.userId) updated else it
                }
            }
        }
    }

    private fun confirmBlock(target: GamePlayer) {
        viewModelScope.launch {
            gameRepository.addGameEvent(GameEvent(
                lobbyId = game.value!!.lobbyId,
                gameId = game.value!!.id,
                senderUserId = player.value!!.userId,
                recipientUserId = target.userId,
                eventType = GameTypeCell.BLOCK,
            ))
            _blocks.value += target
            endTurn()
        }
    }

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            when (val dlg = _dialog.value) {
                is GameDialogData.BlockChoice -> {
                    val target = _players.value.firstOrNull { it.userId == dlg.players[idx].userId }
                    if (target != null) {
                        confirmBlock(target)
                    }
                }
                is GameDialogData.HackerQuestion -> {
                    updatePlayerPoints(-dlg.cost)
                    _dialog.value = null
                }

                is GameDialogData.ChanceQuestion -> {
                    val correct = (idx == dlg.correctIndex)
                    val delta = if (correct) dlg.points else -dlg.points
                    updatePlayerPoints(delta)
                    val title = if (correct) "Corretto!" else "Sbagliato!"
                    val message = if (correct)
                        "Hai guadagnato ${dlg.points} punti."
                    else
                        "Hai perso ${dlg.points} punti."
                    _dialog.value = GameDialogData.Alert(title, message)
                }

                else -> {
                    _dialog.value = null
                }
            }
        }
    }

    fun onResultDismiss() {
        _dialog.value = null
    }

    fun endTurn() {
        _actionsPermitted.value = listOf(
            GameAction(
                id = "wait_your_turn",
                iconRes = R.drawable.ic_stop_hand,
                action = { },
            ),
        )
        _diceRoll.value = null
        nextTurn()
    }
}
