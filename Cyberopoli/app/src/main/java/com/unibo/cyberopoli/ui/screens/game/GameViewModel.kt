package com.unibo.cyberopoli.ui.screens.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GameAction
import com.unibo.cyberopoli.data.models.game.GameAsset
import com.unibo.cyberopoli.data.models.game.GameCell
import com.unibo.cyberopoli.data.models.game.GameDialogData
import com.unibo.cyberopoli.data.models.game.GameEvent
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.data.models.game.GameTypeCell
import com.unibo.cyberopoli.data.models.game.PERIMETER_CELLS
import com.unibo.cyberopoli.data.models.game.PERIMETER_PATH
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class GameViewModel(
    private val app: Application, private val gameRepository: GameRepository
) : ViewModel() {
    val game: LiveData<Game?> = gameRepository.currentGameLiveData
    val turn: LiveData<String?> = gameRepository.currentTurnLiveData

    // Mine variables
    private val player: LiveData<GamePlayer?> = gameRepository.currentPlayerLiveData

    private val _subscriptions = MutableStateFlow<List<GameTypeCell>>(emptyList())
    val subscriptions: StateFlow<List<GameTypeCell>> = _subscriptions.asStateFlow()

    private val _animatedPositions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val animatedPositions: StateFlow<Map<String, Int>> = _animatedPositions.asStateFlow()

    private val _startAnimation = MutableStateFlow(false)
    val startAnimation: StateFlow<Boolean> = _startAnimation.asStateFlow()

    private val _diceRoll = MutableStateFlow<Int?>(null)
    val diceRoll: StateFlow<Int?> = _diceRoll.asStateFlow()

    private val _dialog = MutableStateFlow<GameDialogData?>(null)
    val dialog: StateFlow<GameDialogData?> = _dialog.asStateFlow()

    private val _isLoadingQuestion = MutableStateFlow(false)
    val isLoadingQuestion: StateFlow<Boolean> = _isLoadingQuestion.asStateFlow()

    private val _skipNext = MutableStateFlow(false)
    private val _hasVpn = MutableStateFlow(false)
    private val _playersBlocked = MutableStateFlow<Set<GamePlayer>>(emptySet())
    private val _gameAssets = MutableStateFlow<List<GameAsset>>(emptyList())

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players: StateFlow<List<GamePlayer>> = _players.asStateFlow()

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())

    private val rollDiceAction = GameAction(
        id = "roll_dice",
        iconRes = R.drawable.ic_dice,
        action = { rollDice() },
    )

    private val waitTurnAction = GameAction(
        id = "wait_your_turn",
        iconRes = R.drawable.ic_stop_hand,
        action = { },
    )

    private val _actionsPermitted = MutableStateFlow<List<GameAction>>(emptyList())
    val actionsPermitted: StateFlow<List<GameAction>> = _actionsPermitted.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.preloadQuestionsForUser()
        }
        // Initial turn logic
        turn.asFlow()
            .filterNotNull()
            .onEach { currentTurnId ->
                val currentPlayingPlayer = player.value
                Log.d("TEST GameViewModel", "Turn changed: $currentTurnId, Current player: ${currentPlayingPlayer?.userId}")

                val isMyTurn = if (currentPlayingPlayer != null) {
                    currentTurnId == currentPlayingPlayer.userId
                } else {
                    false
                }

                val currentActionId = _actionsPermitted.value.firstOrNull()?.id
                if (isMyTurn) {
                    if (currentActionId == null || currentActionId == waitTurnAction.id) {
                        if (_skipNext.value) {
                            _skipNext.value = false
                            _actionsPermitted.value = listOf(waitTurnAction)
                            _dialog.value = GameDialogData.Alert(
                                title = app.getString(R.string.broken_router),
                                message = app.getString(R.string.broken_router_desc),
                            )
                        } else {
                            _actionsPermitted.value = listOf(rollDiceAction)
                            if (_hasVpn.value) {
                                _hasVpn.value = false
                                gameRepository.removeGameEvent(
                                    GameEvent(
                                        lobbyId = game.value!!.lobbyId,
                                        gameId = game.value!!.id,
                                        senderUserId = player.value!!.userId,
                                        eventType = GameTypeCell.VPN
                                    )
                                )
                            }
                        }
                        _actionsPermitted.value = listOf(rollDiceAction)
                    }
                } else {
                    if (currentActionId == null || currentActionId != waitTurnAction.id) {
                        _actionsPermitted.value = listOf(waitTurnAction)
                    }
                }
            }.catch { e ->
                Log.e("GameViewModel", "Error observing turn changes: ${e.message}", e)
                _actionsPermitted.value = emptyList()
            }.launchIn(viewModelScope)
    }

    private fun nextTurn() {
        if (game.value == null || _players.value.isEmpty()) return

        viewModelScope.launch {
            _players.value.let { players ->
                val currentTurnUserId = game.value!!.turn
                val currentTurnIndex = players.indexOfFirst { it.userId == currentTurnUserId }
                val nextIdx = (currentTurnIndex + 1) % players.size // Get the next turn index
                val nextPlayerId = players[nextIdx].userId
                gameRepository.setNextTurn(nextPlayerId)
                if (nextPlayerId == player.value?.userId) {
                    _actionsPermitted.value = listOf(rollDiceAction)
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
            gameRepository.createOrGetGame(lobbyId, lobbyMembers)
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
                title = app.getString(R.string.roll_dice),
                message = "${app.getString(R.string.roll_dice_desc)} ${_diceRoll.value}",
            )
            _actionsPermitted.value = listOf(
                GameAction(
                    id = "move_on",
                    iconRes = R.drawable.ic_move_on,
                    action = {
                        startMovementAnimation()
                    },
                ),
            )
        }
    }

    fun movePlayer() {
        viewModelScope.launch {
            if (game.value == null || player.value == null) return@launch
            _players.value.firstOrNull { it.userId == player.value!!.userId }?.let { me ->
                val oldCellPosition = me.cellPosition
                val diceRolled = _diceRoll.value ?: 0
                if (diceRolled <= 0) return@let

                val newPos = computeNewPosition(oldCellPosition, diceRolled)
                val path = PERIMETER_PATH
                val oldPathIndex = path.indexOf(oldCellPosition)

                if (oldPathIndex != -1 && (oldPathIndex + diceRolled) >= path.size) {
                    increasePlayerRound()
                }

                Log.d("GameViewModel", "Player ${me.userId} moved from $oldCellPosition to $newPos. Dice: $diceRolled")
                gameRepository.updatePlayerPosition(newPos)
                _players.value = _players.value.map {
                    if (it.userId == me.userId) it.copy(cellPosition = newPos) else it
                }
                PERIMETER_CELLS[newPos]?.let { landedCell ->
                    handleLanding(landedCell)
                } ?: Log.e("GameViewModel", "Landed on a cell not in PERIMETER_CELLS: $newPos")
            }
        }
    }

    private fun handleLanding(gameCell: GameCell) {
        val gameTypeCell = gameCell.type
        val isCellOwned = _gameAssets.value.any { it.cellId == gameCell.id }
        val amISubscribe = _subscriptions.value.contains(gameTypeCell)
        val amIOwner = _gameAssets.value.any { it.ownerId == player.value?.userId }

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
                    Log.d(
                        "GameViewModel",
                        "Landed on START cell. Lap bonus (if any) handled by movePlayer."
                    )
                }

                GameTypeCell.CHANCE -> {
                    showDialogPerType(GameTypeCell.CHANCE)
                }

                GameTypeCell.HACKER -> {
                    showDialogPerType(GameTypeCell.HACKER)
                }

                GameTypeCell.BLOCK -> {
                    showDialogPerType(GameTypeCell.BLOCK)
                }

                GameTypeCell.VPN -> {
                    _hasVpn.value = true
                    gameRepository.addGameEvent(
                        GameEvent(
                            lobbyId = game.value!!.lobbyId,
                            gameId = game.value!!.id,
                            senderUserId = player.value!!.userId,
                            eventType = GameTypeCell.VPN
                        )
                    )
                    showDialogPerType(GameTypeCell.VPN)
                }

                GameTypeCell.BROKEN_ROUTER -> {
                    showDialogPerType(GameTypeCell.BROKEN_ROUTER)
                }

                else -> {
                    if (!isCellOwned) {
                        if (!amISubscribe) {
                            _actionsPermitted.value += listOf(
                                GameAction(
                                    id = "subscribe_cell",
                                    iconRes = R.drawable.ic_subscribe,
                                    action = {
                                        _dialog.value = GameDialogData.SubscribeChoice(
                                            title = app.getString(R.string.block_player_choice),
                                            options = listOf(
                                                app.getString(R.string.accept), app.getString(R.string.decline)
                                            ),
                                            cost = gameCell.value ?: 0
                                        )

                                    },
                                )
                            )
                        } else {
                            _actionsPermitted.value += listOf(
                                GameAction(
                                    id = "make_content",
                                    iconRes = R.drawable.ic_make_content,
                                    action = {
                                        endTurn()
                                    },
                                )
                            )
                        }
                    } else if (!amIOwner) {
                        if (_hasVpn.value) {
                            _dialog.value = GameDialogData.Alert(
                                title = app.getString(R.string.get_vpn),
                                message = app.getString(R.string.vpn_avoid_pay),
                            )
                        } else {
                            val cellOwner = _gameAssets.value.firstOrNull { asset ->
                                asset.cellId == gameCell.id
                            }?.ownerId!!
                            _dialog.value = GameDialogData.Alert(
                                title = app.getString(R.string.pay_content),
                                message = "${app.getString(R.string.pay_content_desc)} ${gameCell.value} ${app.getString(R.string.internet_points)}",
                            )
                            gameCell.value?.let {
                                Log.d("GameViewModel", "Paying rent: $it to $cellOwner")
                                updatePlayerPoints(-it)
                                updatePlayerPoints(it, cellOwner)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialogPerType(eventType: GameTypeCell) {
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
                updatePlayerPoints(question.points)
            }

            GameTypeCell.BLOCK -> {
                val others = _players.value.filter { it.userId != player.value?.userId }
                _dialog.value = GameDialogData.BlockChoice(
                    title = app.getString(R.string.block_player_choice), players = others
                )
            }

            GameTypeCell.VPN -> {
                _dialog.value = GameDialogData.Alert(
                    title = app.getString(R.string.get_vpn),
                    message = app.getString(R.string.get_vpn_desc),
                )
            }

            GameTypeCell.BROKEN_ROUTER -> {
                _dialog.value = GameDialogData.Alert(
                    title = app.getString(R.string.broken_router),
                    message = app.getString(R.string.broken_router_desc),
                )
                _skipNext.value = true
            }

            else -> {
                throw IllegalStateException("Invalid event type: $eventType")
            }
        }
        _isLoadingQuestion.value = false
    }

    private fun startMovementAnimation() {
        viewModelScope.launch {
            val steps = _diceRoll.value ?: 0

            if (player.value?.userId == null) return@launch
            val currentPlayerId = player.value!!.userId

            if (steps <= 0 || game.value == null) {
                _players.value.firstOrNull { it.userId == currentPlayerId }?.let { me ->
                    PERIMETER_CELLS[me.cellPosition]?.let { landedCell ->
                        handleLanding(landedCell)
                    }
                }
                return@launch
            }

            val path = PERIMETER_PATH
            val playerToAnimate =
                _players.value.firstOrNull { it.userId == currentPlayerId } ?: return@launch
            val originalCellPosition = playerToAnimate.cellPosition
            val startPathIndex = path.indexOf(originalCellPosition).coerceAtLeast(0)

            for (i in 1..steps) {
                val nextAnimatedPosOnPath = path[(startPathIndex + i) % path.size]
                _animatedPositions.value = mapOf(currentPlayerId to nextAnimatedPosOnPath)
                delay(400L)
            }

            val finalNewPos = computeNewPosition(originalCellPosition, steps)

            _players.value = _players.value.map { p ->
                if (p.userId == currentPlayerId) {
                    p.copy(cellPosition = finalNewPos)
                } else {
                    p
                }
            }

            _animatedPositions.value -= currentPlayerId
            gameRepository.updatePlayerPosition(finalNewPos)

            val oldPathIndex = path.indexOf(originalCellPosition)
            if (oldPathIndex != -1 && (oldPathIndex + steps) >= path.size) {
                increasePlayerRound()
            }

            PERIMETER_CELLS[finalNewPos]?.let { landedCell ->
                handleLanding(landedCell)
            } ?: Log.e("GameViewModel", "Landed on a cell not in PERIMETER_CELLS: $finalNewPos")
        }
    }

    private fun increasePlayerRound() {
        viewModelScope.launch {
            gameRepository.increasePlayerRound()
            updatePlayerPoints(+10)
        }
    }

    fun updatePlayerPoints(points: Int) {
        viewModelScope.launch {
            val currentPlayerId = player.value?.userId ?: return@launch
            gameRepository.updatePlayerPoints(points)
            _players.value = _players.value.map { p ->
                if (p.userId == currentPlayerId) {
                    p.copy(score = p.score + points)
                } else {
                    p
                }
            }
        }
    }

    private fun updatePlayerPoints(points: Int, ownerId: String) {
        viewModelScope.launch {
            gameRepository.updatePlayerPoints(points, ownerId)
            val ownerPlayer = _players.value.firstOrNull { it.userId == ownerId }

            val updated = ownerPlayer?.copy(
                score = ownerPlayer.score + points
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
            gameRepository.addGameEvent(
                GameEvent(
                    lobbyId = game.value!!.lobbyId,
                    gameId = game.value!!.id,
                    senderUserId = player.value!!.userId,
                    recipientUserId = target.userId,
                    eventType = GameTypeCell.BLOCK,
                )
            )
            _playersBlocked.value += target
            endTurn()
        }
    }

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            when (val dlg = _dialog.value) {
                is GameDialogData.SubscribeChoice -> {
                    if (idx == 0) {
                        updatePlayerPoints(-dlg.cost)
                        _actionsPermitted.value = _actionsPermitted.value.dropLast(1)
                        _subscriptions.value += player.value?.let { PERIMETER_CELLS[it.cellPosition]?.type }!!
                        Log.d("GameViewModel", "Subscribed to ${_subscriptions.value}")
                    }
                    onResultDismiss()
                }

                is GameDialogData.BlockChoice -> {
                    if (idx >= 0 && idx < dlg.players.size) {
                        val target = dlg.players[idx]
                        val actualTarget = _players.value.firstOrNull { it.userId == target.userId }
                        if (actualTarget != null) {
                            confirmBlock(actualTarget)
                        }
                    }
                    onResultDismiss()
                }

                is GameDialogData.HackerStatement -> {
                    updatePlayerPoints(-dlg.points)
                    _dialog.value = null
                }

                is GameDialogData.ChanceQuestion -> {
                    val correct = (idx == dlg.correctIndex)
                    val delta = if (correct) dlg.points else -dlg.points
                    updatePlayerPoints(delta)
                    val resultTitle =
                        if (correct) app.getString(R.string.correct_answer) else app.getString(R.string.wrong_answer)
                    val resultMessage = if (correct) {
                        "${app.getString(R.string.points_earned)} ${dlg.points} ${app.getString(R.string.internet_points)}"
                    } else {
                        "${app.getString(R.string.points_lost)} ${dlg.points} ${app.getString(R.string.internet_points)}"
                    }
                    _dialog.value =
                        GameDialogData.Alert(title = resultTitle, message = resultMessage)
                }

                is GameDialogData.Alert -> {
                    onResultDismiss()
                }

                else -> onResultDismiss()
            }
        }
    }

    fun onResultDismiss() {
        _dialog.value = null
    }

    fun endTurn() {
        Log.d("GameViewModel", "Ending turn.")
        _actionsPermitted.value = listOf(waitTurnAction)
        _diceRoll.value = null
        nextTurn()
    }
}
