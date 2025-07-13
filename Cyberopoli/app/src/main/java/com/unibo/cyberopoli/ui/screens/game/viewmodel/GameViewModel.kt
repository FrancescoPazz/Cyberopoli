package com.unibo.cyberopoli.ui.screens.game.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
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
import com.unibo.cyberopoli.data.models.lobby.LobbyStatus
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.lobby.LobbyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameViewModel(
    lobbyRepository: LobbyRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {
    val lobby: StateFlow<Lobby?> = lobbyRepository.currentLobby
    val game: LiveData<Game?> = gameRepository.currentGameLiveData
    val cells = mutableStateOf(createBoard())
    private var chanceQuestions = mutableStateOf(chanceQuestions())
    private var hackerStatements = mutableStateOf(hackerStatements())

    // Mine variables
    val player: LiveData<GamePlayer?> = gameRepository.currentPlayerLiveData
    val players: LiveData<List<GamePlayer>> = gameRepository.currentPlayersLiveData
    val events: LiveData<List<GameEvent>> = gameRepository.currentGameEventsLiveData
    val assets: LiveData<List<GameAsset>> = gameRepository.currentGameAssetsLiveData

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

    private val _gameOver = mutableStateOf(false)
    val gameOver: State<Boolean> = _gameOver

    private val _skipNext = MutableStateFlow(false)
    private val _hasVpn = MutableStateFlow(false)
    private val _playersBlocked = MutableStateFlow<Set<GamePlayer>>(emptySet())
    private val _previousTurn = MutableStateFlow<String?>(null)

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

    private val passTurnAction = GameAction(
        id = "turn_pass",
        iconRes = R.drawable.ic_skip,
        action = { endTurn() },
    )

    private val _actionsPermitted = MutableStateFlow<List<GameAction>>(emptyList())
    val actionsPermitted: StateFlow<List<GameAction>> = _actionsPermitted.asStateFlow()

    init {
        viewModelScope.launch {
            val generatedHackerStatement = gameRepository.generateDigitalWellBeingStatements()
            hackerStatements.value = hackerStatements.value.plus(generatedHackerStatement)
        }
        // Initial turn logic
        viewModelScope.launch {
            player.asFlow().combine(game.asFlow()) { playerValue, gameValue -> // Game Flow
                Pair(playerValue, gameValue)
            }.filterNotNull().filter { (p, g) -> p != null && g != null }
                .onEach { (currentPlayer, currentGame) ->
                    Log.d(
                        "TEST GameViewModel",
                        "Combined Flow: Game changed: $currentGame, Current player: ${currentPlayer!!.userId}"
                    )

                    val isMyTurn = currentGame!!.turn == currentPlayer.userId
                    val currentActionId = _actionsPermitted.value.firstOrNull()?.id
                    val turnChanged = _previousTurn.value != currentGame.turn

                    if (turnChanged) {
                        _previousTurn.value = currentGame.turn

                        if (isMyTurn) {
                            if (_skipNext.value) {
                                _skipNext.value = false
                                _actionsPermitted.value = listOf(waitTurnAction)
                                _dialog.value = GameDialogData.Alert(
                                    titleRes = R.string.broken_router,
                                    messageRes = R.string.broken_router_desc,
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
                    Log.e(
                        "GameViewModel",
                        "Error observing combined turn/player changes: ${e.message}",
                        e
                    )
                    _actionsPermitted.value = emptyList()
                }.launchIn(viewModelScope)
        }

        viewModelScope.launch {
            lobby.filterNotNull().map { it.status }.distinctUntilChanged()
                .onEach { newStatus ->
                    Log.d("testlbbd GameViewModel", "Lobby status changed: $newStatus")
                    if (newStatus == LobbyStatus.FINISHED.value) {
                        _gameOver.value = true
                        val player = player.value ?: return@onEach
                        player.user.let { userId ->
                            Log.d("provolone", "Saving user progress for user: $userId")
                            gameRepository.saveUserProgress()
                            gameRepository.clearGameData()
                        }
                    }
                }.launchIn(viewModelScope)
        }

        viewModelScope.launch {
            gameRepository.currentGameEventsLiveData.asFlow().filterNotNull().distinctUntilChanged()
                .collect { events ->
                    Log.d("GameViewModel", "GameEvents aggiornati: $events")
                }
        }

        viewModelScope.launch {
            gameRepository.currentGameAssetsLiveData.asFlow().filterNotNull().distinctUntilChanged()
                .collect { assets ->
                    Log.d("GameViewModel", "GameAssets aggiornati: $assets")
                }
        }
    }

    fun resetGame() {
        _gameOver.value = false
        _startAnimation.value = false
        _diceRoll.value = null
        _dialog.value = null
        _isLoadingQuestion.value = false
        _skipNext.value = false
        _hasVpn.value = false
        _playersBlocked.value = emptySet()
        _previousTurn.value = null
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
            _diceRoll.value = 6//(1..6).random()
            _dialog.value = GameDialogData.Alert(
                titleRes = R.string.roll_dice,
                messageRes = R.string.roll_dice_desc,
                messageArgs = listOf(_diceRoll.value.toString()),
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

    fun movePlayer() {
        viewModelScope.launch {
            val currentPlayer = player.value ?: return@launch
            val me =
                players.value?.firstOrNull { it.userId == currentPlayer.userId } ?: return@launch

            val oldCellPosition = me.cellPosition
            val diceRolled = _diceRoll.value ?: 0
            if (diceRolled <= 0) return@launch

            val path = PERIMETER_PATH
            val oldPathIndex = path.indexOf(oldCellPosition)
            if (oldPathIndex == -1) return@launch

            val steps = (1..diceRolled).map { step ->
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
        val isCellOwned = assets.value?.any { it.cellId == gameCell.id }
        val amISubscribe = _subscriptions.value.contains(gameTypeCell)
        val amIOwner = assets.value?.any { it.ownerId == player.value?.userId }

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
                    _hasVpn.value = true
                    gameRepository.addGameEvent(
                        GameEvent(
                            lobbyId = game.value!!.lobbyId,
                            lobbyCreatedAt = game.value!!.lobbyCreatedAt,
                            gameId = game.value!!.id,
                            senderUserId = player.value!!.userId,
                            eventType = GameTypeCell.VPN,
                        ),
                    )
                    showDialogPerType(GameTypeCell.VPN)
                }

                GameTypeCell.BROKEN_ROUTER -> {
                    showDialogPerType(GameTypeCell.BROKEN_ROUTER)
                }

                else -> {
                    if (!isCellOwned!!) {
                        if (!amISubscribe) {
                            _actionsPermitted.value += listOf(
                                GameAction(
                                    id = "subscribe",
                                    iconRes = R.drawable.ic_subscribe,
                                    action = {
                                        _dialog.value = GameDialogData.SubscribeChoice(
                                            titleRes = R.string.subscribe,
                                            messageRes = R.string.subscribe_desc,
                                            messageArgs = listOf(gameCell.value.toString()),
                                            optionsRes = listOf(
                                                R.string.accept,
                                                R.string.decline,
                                            ),
                                            cost = gameCell.value ?: 0,
                                        )
                                    },
                                ),
                            )
                        } else {
                            _actionsPermitted.value += listOf(
                                GameAction(
                                    id = "make_content",
                                    iconRes = R.drawable.ic_make_content,
                                    action = {
                                        _dialog.value = GameDialogData.MakeContentChoice(
                                            titleRes = R.string.make_content,
                                            messageRes = R.string.make_content_desc,
                                            messageArgs = listOf(gameCell.value.toString(), gameCell.value?.times(2).toString()),
                                            optionsRes = listOf(
                                                R.string.accept,
                                                R.string.decline,
                                            ),
                                            cost = (gameCell.value?.times(2) ?: 0),
                                        )

                                        endTurn()
                                    },
                                ),
                            )
                        }
                    } else if (!amIOwner!!) {
                        if (_hasVpn.value) {
                            _dialog.value = GameDialogData.Alert(
                                titleRes = R.string.get_vpn,
                                messageRes = R.string.vpn_avoid_pay,
                            )
                        } else {
                            val cellOwner = assets.value?.firstOrNull { asset ->
                                asset.cellId == gameCell.id
                            }?.ownerId!!
                            _dialog.value = GameDialogData.Alert(
                                titleRes = R.string.pay_content,
                                messageRes = R.string.pay_content_desc,
                            )
                            gameCell.value?.let {
                                Log.d("GameViewModel", "Paying rent: $it to $cellOwner")
                                updatePlayerScore(-it)
                                updatePlayerScore(it, cellOwner)
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
                val questions = chanceQuestions.value
                if (questions.isEmpty()) {
                    throw IllegalStateException("No questions available for event type: $eventType")
                }
                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                chanceQuestions.value = updatedQuestions
                _dialog.value = question
            }

            GameTypeCell.HACKER -> {
                val questions = hackerStatements.value
                check(questions.isNotEmpty()) { "No questions available for event type: $eventType" }

                val randomIndex = questions.indices.random()
                val question = questions[randomIndex]
                val updatedQuestions = questions.toMutableList().apply { removeAt(randomIndex) }
                hackerStatements.value = updatedQuestions
                _dialog.value = question
            }

            GameTypeCell.BLOCK -> {
                val others = players.value?.filter { it.userId != player.value?.userId }
                _dialog.value = others?.let {
                    GameDialogData.BlockChoice(
                        titleRes = R.string.block_player_choice, players = it,
                    )
                }
            }

            GameTypeCell.VPN -> {
                _dialog.value = GameDialogData.Alert(
                    titleRes = R.string.get_vpn,
                    messageRes = R.string.get_vpn_desc,
                )
            }

            GameTypeCell.BROKEN_ROUTER -> {
                _dialog.value = GameDialogData.Alert(
                    titleRes = R.string.broken_router,
                    messageRes = R.string.broken_router_desc,
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
            Log.d("TESTEA GameViewModel", "Player round increased to ${player.value?.round}")

            checkAndRemoveExpiredAssets()

            if (player.value?.round == 2) {
                gameRepository.gameOver()
            }
            updatePlayerScore(+10)

            _hasVpn.value = false
            gameRepository.removeGameEvent(
                GameEvent(
                    lobbyId = game.value!!.lobbyId,
                    lobbyCreatedAt = game.value!!.lobbyCreatedAt,
                    gameId = game.value!!.id,
                    senderUserId = player.value!!.userId,
                    eventType = GameTypeCell.VPN,
                ),
            )
        }
    }

    private fun checkAndRemoveExpiredAssets() {
        viewModelScope.launch {
            val currentRound = player.value?.round ?: return@launch
            val currentAssets = assets.value ?: return@launch

            val expiredAssets = currentAssets.filter { asset ->
                asset.expiresAtRound >= currentRound
            }

            expiredAssets.forEach { expiredAsset ->
                try {
                    gameRepository.removeGameAsset(expiredAsset)
                    Log.d("GameViewModel", "Removed expired asset: ${expiredAsset.cellId}")
                } catch (e: Exception) {
                    Log.e("GameViewModel", "Error removing expired asset: ${e.message}")
                }
            }

            if (expiredAssets.isNotEmpty()) {
                Log.d("GameViewModel", "Removed ${expiredAssets.size} expired assets")
            }
        }
    }

    fun updatePlayerScore(points: Int) {
        viewModelScope.launch {
            gameRepository.updatePlayerScore(points)
        }
    }

    private fun updatePlayerScore(
        points: Int,
        ownerId: String,
    ) {
        viewModelScope.launch {
            gameRepository.updatePlayerScore(points, ownerId)
        }
    }

    private fun confirmBlock(target: GamePlayer) {
        viewModelScope.launch {
            gameRepository.addGameEvent(
                GameEvent(
                    lobbyId = game.value!!.lobbyId,
                    lobbyCreatedAt = game.value!!.lobbyCreatedAt,
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
                    cells.value = cells.value.toMutableList().apply {
                        val position =
                            getAssetPositionFromPerimeterPosition(player.value!!.cellPosition)
                        if (position != null) {
                            this[position] = GameCell(
                                player.value!!.cellPosition.toString(),
                                GameTypeCell.OCCUPIED,
                                "Occupied"
                            )
                            gameRepository.addGameAsset(
                                GameAsset(
                                    lobbyId = game.value!!.lobbyId,
                                    lobbyCreatedAt = game.value!!.lobbyCreatedAt,
                                    gameId = game.value!!.id,
                                    cellId = player.value!!.cellPosition.toString(),
                                    ownerId = player.value!!.userId,
                                    placedAtRound = player.value!!.round,
                                    expiresAtRound = player.value!!.round + 1,
                                )
                            )
                        }
                    }
                    onResultDismiss()
                }

                is GameDialogData.SubscribeChoice -> {
                    if (idx == 0) {
                        updatePlayerScore(-dlg.cost)
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
                    if (dlg.points > 0) {
                        updatePlayerScore(-dlg.points)
                    } else {
                        updatePlayerScore(dlg.points)
                    }
                    _dialog.value = null
                }

                is GameDialogData.ChanceQuestion -> {
                    val correct = (idx == dlg.correctIndex)
                    val delta = if (correct) dlg.points else -dlg.points
                    updatePlayerScore(delta)
                    val resultTitle =
                        if (correct) R.string.correct_answer else R.string.wrong_answer
                    val resultMessage = if (correct) {
                        R.string.points_earned_message
                    } else {
                        R.string.points_lost_message
                    }


                    Log.d("PKAOS", "QuestionResultDialog: $resultMessage, delta: $delta")
                    _dialog.value = GameDialogData.QuestionResult(
                        titleRes = resultTitle,
                        messageRes = resultMessage,
                        messageArgs = listOf(abs(delta).toString()),
                        optionsRes = dlg.optionsRes,
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
