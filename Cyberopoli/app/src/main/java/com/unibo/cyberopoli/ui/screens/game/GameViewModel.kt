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
import com.unibo.cyberopoli.data.models.game.Phase
import com.unibo.cyberopoli.data.models.lobby.LobbyMember
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import com.unibo.cyberopoli.data.repositories.profile.UserRepository
import com.unibo.cyberopoli.data.services.HFService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

sealed class GameDialogData {
    data class Question(
        val title: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int
    ) : GameDialogData()
    data class Result(val title: String, val message: String) : GameDialogData()
}

class GameViewModel(
    private val userRepository: UserRepository,
    private val repo: GameRepository
) : ViewModel() {
    private val hfService = HFService("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")

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players: StateFlow<List<GamePlayer>> = _players.asStateFlow()

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events: StateFlow<List<GameEvent>> = _events.asStateFlow()

    private val _phase = MutableStateFlow(Phase.ROLL_DICE)
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    private val _diceRoll = MutableStateFlow<Int?>(null)
    val diceRoll: StateFlow<Int?> = _diceRoll.asStateFlow()

    private val _landed = MutableStateFlow<CellType>(CellType.START)
    private val landedCellType: StateFlow<CellType> = _landed.asStateFlow()

    private val _dialog = MutableStateFlow<GameDialogData?>(null)
    val dialog: StateFlow<GameDialogData?> = _dialog.asStateFlow()

    private val _isLoadingQuestion = MutableStateFlow(false)
    val isLoadingQuestion: StateFlow<Boolean> = _isLoadingQuestion.asStateFlow()

    private var pendingGameEvent: GameEvent? = null

    fun startGame(lobbyId: String, lobbyMembers: List<LobbyMember>) =
        viewModelScope.launch {
            val newGame = repo.createGame(lobbyId, lobbyMembers)
            _game.value = newGame
            joinGame()
            refreshGameState()
        }

    private suspend fun joinGame() {
        _game.value?.let { repo.joinGame(it, userId) }
        refreshPlayers()
    }

    fun rollDice() {
        _diceRoll.value = (1..6).random()
        _phase.value = Phase.MOVE
    }

    fun movePlayer() = viewModelScope.launch {
        _game.value?.let { game ->
            _players.value.firstOrNull { it.userId == userId }?.let { me ->
                val newPos = computeNewPosition(me.cellPosition, _diceRoll.value ?: 0)
                repo.updatePlayer(game, me.copy(cellPosition = newPos))
                refreshGameState()
                handleLanding(PERIMETER_CELLS[newPos]?.type)
            }
        }
    }

    private fun computeNewPosition(current: Int, roll: Int): Int {
        val path = PERIMETER_PATH
        val idx = (path.indexOf(current).coerceAtLeast(0) + roll) % path.size
        return path[idx]
    }

    private fun handleLanding(cellType: CellType?) {
        when (cellType) {
            CellType.CHANCE -> askQuestion(
                title = "Domanda Sicurezza",
                prompt = buildChoicePrompt(
                    "una domanda a scelta multipla sulla sicurezza informatica",
                    3
                ),
                points = 5,
                eventType = GameEventType.CHANCE
            )
            CellType.HACKER -> askQuestion(
                title = "Scenario Hacker",
                prompt = buildChoicePrompt(
                    "uno scenario di attacco hacker e proponi 3 azioni possibili",
                    3
                ),
                points = 10,
                eventType = GameEventType.HACKER
            )
            else -> _phase.value = Phase.END_TURN
        }
    }

    private fun buildChoicePrompt(description: String, optionsCount: Int): String =
        "Genera $description. " +
                "La voglio in questo formato specifico: DOMANDA==<testo>||OPZIONI==<${optionsCount} opzioni separate da ';;'>||CORRETTA==<indice che parte da 0>"

    private fun askQuestion(
        title: String,
        prompt: String,
        points: Int,
        eventType: GameEventType
    ) = viewModelScope.launch {
        _isLoadingQuestion.value = true
        try {
            val raw = hfService.generateChat(model = "deepseek/deepseek-prover-v2-671b", userPrompt = prompt)
            val (question, options, correct) = parseStructured(raw)
            pendingGameEvent = GameEvent(
                lobbyId = _game.value!!.lobbyId,
                gameId = _game.value!!.id,
                senderUserId = userId,
                recipientUserId = userId,
                eventType = eventType,
                value = points,
                createdAt = Instant.now().toString()
            )
            _dialog.value = GameDialogData.Question(title, question, options, correct)
        } finally {
            _isLoadingQuestion.value = false
        }
    }

    fun onDialogOptionSelected(idx: Int) = viewModelScope.launch {
        (dialog.value as? GameDialogData.Question)?.let { q ->
            val correct = (idx == q.correctIndex)
            val delta = if (correct) q.correctIndex else -q.correctIndex
            pendingGameEvent?.copy(value = delta)?.also { repo.addGameEvent(it) }
            val title = if (correct) "Corretto!" else "Sbagliato!"
            val message = if (correct) "Hai guadagnato $delta punti." else "Hai perso ${-delta} punti."
            _dialog.value = GameDialogData.Result(title, message)
        }
    }

    fun onResultDismiss() {
        _dialog.value = null
        endTurn()
    }

    fun endTurn() {
        _diceRoll.value = null
        _phase.value = Phase.WAIT
        nextTurn()
    }

    private fun nextTurn() = viewModelScope.launch {
        _game.value?.let { game ->
            _players.value.let { players ->
                val nextIdx = (players.indexOfFirst { it.userId == game.turn } + 1) % players.size
                repo.setNextTurn(game, players[nextIdx].userId)
                refreshGameState()
            }
        }
    }

    private fun refreshGameState() {
        viewModelScope.launch { refreshPlayers(); refreshEvents() }
        _phase.value = if (userId == _game.value?.turn) Phase.ROLL_DICE else Phase.WAIT
    }

    private fun refreshPlayers() = viewModelScope.launch {
        _game.value?.id?.let { _players.value = repo.getGamePlayers(it) }
    }

    private fun refreshEvents() = viewModelScope.launch {
        _game.value?.let { _events.value = repo.getGameEvents(it.lobbyId, it.id) }
    }

    private fun parseStructured(raw: String): Triple<String, List<String>, Int> {
        val parts = raw.split("||").map { it.trim() }
        val question = parts[0].substringAfter("DOMANDA==").trim()
        val options = parts[1].substringAfter("OPZIONI==").split(";;").map { it.trim() }
        val correct = parts[2].substringAfter("CORRETTA==").toIntOrNull() ?: 0
        return Triple(question, options, correct)
    }

    private val userId: String
        get() = userRepository.currentUserLiveData.value?.id.orEmpty()
}
