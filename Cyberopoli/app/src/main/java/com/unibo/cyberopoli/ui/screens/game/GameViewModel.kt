package com.unibo.cyberopoli.ui.screens.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
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
import com.unibo.cyberopoli.data.models.game.createBoard
import com.unibo.cyberopoli.data.models.game.getAssetPositionFromPerimeterPosition
import com.unibo.cyberopoli.data.models.game.questions.chanceQuestions
import com.unibo.cyberopoli.data.models.game.questions.hackerStatements
import com.unibo.cyberopoli.data.models.lobby.Lobby
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameViewModel(
    private val app: Application,
    private val gameRepository: GameRepository,
) : ViewModel() {
    val game: LiveData<Game?> = gameRepository.currentGameLiveData
    val cells: MutableLiveData<List<GameCell>> = MutableLiveData(createBoard())
    val chanceQuestions = MutableLiveData(chanceQuestions(app))
    val hackerStatements = MutableLiveData(hackerStatements(app))

    // Mine variables
    val player: LiveData<GamePlayer?> = gameRepository.currentPlayerLiveData
    val players: LiveData<List<GamePlayer>> = gameRepository.currentPlayersLiveData

    private val _subscriptions = MutableStateFlow<List<GameTypeCell>>(emptyList())
    val subscriptions: StateFlow<List<GameTypeCell>> = _subscriptions.asStateFlow()

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
    private val _previousTurn = MutableStateFlow<String?>(null)
    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())

    private val rollDiceAction =
        GameAction(
            id = "roll_dice",
            iconRes = R.drawable.ic_dice,
            action = { rollDice() },
        )

    private val waitTurnAction =
        GameAction(
            id = "wait_your_turn",
            iconRes = R.drawable.ic_stop_hand,
            action = { },
        )

    private val passTurnAction =
        GameAction(
            id = "turn_pass",
            iconRes = R.drawable.ic_skip,
            action = { endTurn() },
        )

    private val _actionsPermitted = MutableStateFlow<List<GameAction>>(emptyList())
    val actionsPermitted: StateFlow<List<GameAction>> = _actionsPermitted.asStateFlow()

    init {
        viewModelScope.launch {
            val generatedHackerStatement = gameRepository.generateDigitalWellBeingStatements()
            hackerStatements.value =
                hackerStatements.value?.plus(generatedHackerStatement) ?: hackerStatements.value
        }
        // Initial turn logic
        viewModelScope.launch {
            player.asFlow()
                .combine(game.asFlow()) { playerValue, gameValue -> // game flow
                    Pair(playerValue, gameValue)
                }
                .filterNotNull()
                .filter { (p, g) -> p != null && g != null }
                .onEach { (currentPlayer, currentGame) ->
                    Log.d("TEST GameViewModel", "Combined Flow: Game changed: $currentGame, Current player: ${currentPlayer!!.userId}")

                    val isMyTurn = currentGame!!.turn == currentPlayer.userId
                    val currentActionId = _actionsPermitted.value.firstOrNull()?.id
                    val turnChanged = _previousTurn.value != currentGame.turn

                    if (turnChanged) {
                        _previousTurn.value = currentGame.turn

                        if (isMyTurn) {
                            if (_skipNext.value) {
                                _skipNext.value = false
                                _actionsPermitted.value = listOf(waitTurnAction)
                                _dialog.value =
                                    GameDialogData.Alert(
                                        title = app.getString(R.string.broken_router),
                                        message = app.getString(R.string.broken_router_desc),
                                    )
                                nextTurn()
                            } else {
                                _actionsPermitted.value = listOf(rollDiceAction)
                            }
                        } else {
                            if (currentActionId == null || currentActionId != waitTurnAction.id) {
                                _actionsPermitted.value = listOf(waitTurnAction)
                            }
                        }
                    }
                }.catch { e ->
                    Log.e("GameViewModel", "Error observing combined turn/player changes: ${e.message}", e)
                    _actionsPermitted.value = emptyList()
                }.launchIn(viewModelScope)
        }
    }

    private fun nextTurn() {
        if (game.value == null || players.value == null || players.value!!.isEmpty()) return

        viewModelScope.launch {
            players.value.let { players ->
                val currentTurnUserId = game.value!!.turn
                val currentTurnIndex = players!!.indexOfFirst { it.userId == currentTurnUserId }
                val nextIdx = (currentTurnIndex + 1) % players.size
                val nextPlayerId = players[nextIdx].userId
                gameRepository.setNextTurn(nextPlayerId)
                if (nextPlayerId == player.value?.userId) {
                    _actionsPermitted.value = listOf(rollDiceAction)
                }
            }
        }
    }

    fun startGame(
        passedLobby: Lobby,
        lobbyMembers: List<LobbyMember>,
    ) {
        viewModelScope.launch {
            gameRepository.createOrGetGame(passedLobby, lobbyMembers)
            joinGame()
        }
    }

    private fun joinGame() {
        viewModelScope.launch {
            gameRepository.joinGame()
        }
    }

    fun rollDice() {
        viewModelScope.launch {
            _diceRoll.value = (1..6).random()
            _dialog.value =
                GameDialogData.Alert(
                    title = app.getString(R.string.roll_dice),
                    message = "${app.getString(R.string.roll_dice_desc)} ${_diceRoll.value}",
                )
            _actionsPermitted.value =
                listOf(
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

    fun movePlayer() {
        viewModelScope.launch {
            val currentPlayer = player.value ?: return@launch
            val me =
                players.value
                    ?.firstOrNull { it.userId == currentPlayer.userId }
                    ?: return@launch

            val oldCellPosition = me.cellPosition
            val diceRolled = _diceRoll.value ?: 0
            if (diceRolled <= 0) return@launch

            val path = PERIMETER_PATH
            val oldPathIndex = path.indexOf(oldCellPosition)
            if (oldPathIndex == -1) return@launch

            val steps =
                (1..diceRolled).map { step ->
                    path[(oldPathIndex + step) % path.size]
                }

            val animationDelayMs = 200L
            for (newPos in steps) {
                Log.d("GameViewModel", "Animating move to $newPos")
                gameRepository.updatePlayerPosition(newPos)
                delay(animationDelayMs)
            }

            if (oldPathIndex + diceRolled >= path.size) {
                increasePlayerRound()
            }

            Log.d("GameViewModel", "Player ${me.userId} landed on ${steps.last()}")
            PERIMETER_CELLS[steps.last()]?.let { landedCell ->
                handleLanding(landedCell)
            } ?: Log.e("GameViewModel", "Landed on a cell not in PERIMETER_CELLS: ${steps.last()}")
        }
    }

    private fun handleLanding(gameCell: GameCell) {
        val gameTypeCell = gameCell.type
        val isCellOwned = _gameAssets.value.any { it.cellId == gameCell.id }
        val amISubscribe = _subscriptions.value.contains(gameTypeCell)
        val amIOwner = _gameAssets.value.any { it.ownerId == player.value?.userId }

        viewModelScope.launch {
            _actionsPermitted.value =
                listOf(
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
                        "Landed on START cell.",
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
                    if (_hasVpn.value) {
                        _hasVpn.value = false
                        gameRepository.removeGameEvent(
                            GameEvent(
                                lobbyId = game.value!!.lobbyId,
                                gameId = game.value!!.id,
                                senderUserId = player.value!!.userId,
                                eventType = GameTypeCell.VPN,
                            ),
                        )
                        showDialogPerType(GameTypeCell.VPN)
                    } else {
                        _hasVpn.value = true
                        gameRepository.addGameEvent(
                            GameEvent(
                                lobbyId = game.value!!.lobbyId,
                                gameId = game.value!!.id,
                                senderUserId = player.value!!.userId,
                                eventType = GameTypeCell.VPN,
                            ),
                        )
                    }
                }

                GameTypeCell.BROKEN_ROUTER -> {
                    showDialogPerType(GameTypeCell.BROKEN_ROUTER)
                }

                else -> {
                    if (!isCellOwned) {
                        if (!amISubscribe) {
                            _actionsPermitted.value +=
                                listOf(
                                    GameAction(
                                        id = "subscribe",
                                        iconRes = R.drawable.ic_subscribe,
                                        action = {
                                            _dialog.value =
                                                GameDialogData.SubscribeChoice(
                                                    title = app.getString(R.string.subscribe),
                                                    message = app.resources.getString(R.string.subscribe_desc, gameCell.value!!),
                                                    options =
                                                        listOf(
                                                            app.getString(R.string.accept), app.getString(R.string.decline),
                                                        ),
                                                    cost = gameCell.value ?: 0,
                                                )
                                        },
                                    ),
                                )
                        } else {
                            _actionsPermitted.value +=
                                listOf(
                                    GameAction(
                                        id = "make_content",
                                        iconRes = R.drawable.ic_make_content,
                                        action = {
                                            _dialog.value =
                                                GameDialogData.MakeContentChoice(
                                                    title = app.getString(R.string.make_content),
                                                    message = app.resources.getString(R.string.make_content_desc, gameCell.value!!, gameCell.value * 2),
                                                    options =
                                                        listOf(
                                                            app.getString(R.string.accept), app.getString(R.string.decline),
                                                        ),
                                                    cost = (gameCell.value.times(2)),
                                                )

                                            endTurn()
                                        },
                                    ),
                                )
                        }
                    } else if (!amIOwner) {
                        if (_hasVpn.value) {
                            _dialog.value =
                                GameDialogData.Alert(
                                    title = app.getString(R.string.get_vpn),
                                    message = app.getString(R.string.vpn_avoid_pay),
                                )
                        } else {
                            val cellOwner =
                                _gameAssets.value.firstOrNull { asset ->
                                    asset.cellId == gameCell.id
                                }?.ownerId!!
                            _dialog.value =
                                GameDialogData.Alert(
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
                val questions = chanceQuestions.value.orEmpty()
                if (questions.isEmpty()) {
                    throw IllegalStateException("No questions available for event type: $eventType")
                }
                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                chanceQuestions.postValue(updatedQuestions)
                _dialog.value = question
            }

            GameTypeCell.HACKER -> {
                val questions = hackerStatements.value.orEmpty()
                check(questions.isNotEmpty()) { "No questions available for event type: $eventType" }

                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                hackerStatements.postValue(updatedQuestions)
                _dialog.value = question
                updatePlayerPoints(question.points)
            }

            GameTypeCell.BLOCK -> {
                val others = players.value?.filter { it.userId != player.value?.userId }
                _dialog.value =
                    others?.let {
                        GameDialogData.BlockChoice(
                            title = app.getString(R.string.block_player_choice), players = it,
                        )
                    }
            }

            GameTypeCell.VPN -> {
                _dialog.value =
                    GameDialogData.Alert(
                        title = app.getString(R.string.get_vpn),
                        message = app.getString(R.string.get_vpn_desc),
                    )
            }

            GameTypeCell.BROKEN_ROUTER -> {
                _dialog.value =
                    GameDialogData.Alert(
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

    private fun increasePlayerRound() {
        viewModelScope.launch {
            gameRepository.increasePlayerRound()
            updatePlayerPoints(+10)
        }
    }

    fun updatePlayerPoints(points: Int) {
        viewModelScope.launch {
            gameRepository.updatePlayerPoints(points)
        }
    }

    private fun updatePlayerPoints(
        points: Int,
        ownerId: String,
    ) {
        viewModelScope.launch {
            gameRepository.updatePlayerPoints(points, ownerId)
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
                ),
            )
            _playersBlocked.value += target
            endTurn()
        }
    }

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            when (val dlg = _dialog.value) {
                is GameDialogData.MakeContentChoice -> {
                    cells.value =
                        cells.value?.toMutableList()?.apply {
                            val position = getAssetPositionFromPerimeterPosition(player.value!!.cellPosition)
                            if (position != null) {
                                this[position] = GameCell(player.value!!.cellPosition.toString(), GameTypeCell.OCCUPIED, "Occupied")
                                _gameAssets.value +=
                                    GameAsset(
                                        lobbyId = game.value!!.lobbyId,
                                        gameId = game.value!!.id,
                                        cellId = player.value!!.cellPosition.toString(),
                                        ownerId = player.value!!.userId,
                                        placedAtRound = player.value!!.round,
                                        expiresAtRound = player.value!!.round + 1,
                                    )
                            }
                        }
                    onResultDismiss()
                }

                is GameDialogData.SubscribeChoice -> {
                    if (idx == 0) {
                        updatePlayerPoints(-dlg.cost)
                        _subscriptions.value += player.value?.let { PERIMETER_CELLS[it.cellPosition]?.type }!!
                        Log.d("GameViewModel", "Subscribed to ${_subscriptions.value}")
                        _actionsPermitted.value = listOf(
                            passTurnAction
                        )

                    }
                    onResultDismiss()
                }

                is GameDialogData.BlockChoice -> {
                    if (idx >= 0 && idx < dlg.players.size) {
                        val target = dlg.players[idx]
                        val actualTarget = players.value?.firstOrNull { it.userId == target.userId }
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
                    val resultMessage =
                        if (correct) {
                            "${app.getString(R.string.points_earned)} ${dlg.points} ${app.getString(R.string.internet_points)}"
                        } else {
                            "${app.getString(R.string.points_lost)} ${dlg.points} ${app.getString(R.string.internet_points)}"
                        }

                    _dialog.value = GameDialogData.QuestionResult(
                        title = resultTitle,
                        message = resultMessage,
                        options = dlg.options,
                        correctIndex = dlg.correctIndex,
                        selectedIndex = idx
                    )
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
