package com.unibo.cyberopoli.ui.screens.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.R
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
import com.unibo.cyberopoli.data.services.HFService
import com.unibo.cyberopoli.util.UsageStatsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class GameViewModel(
    private val gameRepository: GameRepository,
    private val usageStatsHelper: UsageStatsHelper
) : ViewModel() {
    private val hfService = HFService("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")

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

    private fun handleLanding(context: Context, gameEventType: GameEventType?) {
        viewModelScope.launch {
            when (gameEventType) {
                GameEventType.START -> {
                    gameRepository.updatePlayerPoints(50)
                }
                GameEventType.CHANCE -> askQuestion(
                    title = context.getString(R.string.security_question),
                    description = context.getString(R.string.security_description),
                    points = 5,
                    eventType = GameEventType.CHANCE
                )
                GameEventType.HACKER -> askQuestion(
                    title = context.getString(R.string.hacker_attack),
                    description = context.getString(R.string.hacker_attack_description),
                    points = 10,
                    eventType = GameEventType.HACKER
                )
                else -> {

                }
            }
        }
    }

    private fun askQuestion(
        title: String,
        description: String,
        points: Int,
        eventType: GameEventType,
    ) {
        viewModelScope.launch {
            _isLoadingQuestion.value = true
            try {
                val topApps = usageStatsHelper.getTopUsedApps()
                    .joinToString("; ") { "${it.first}:${it.second / 1000}s" }
                val totalSec = usageStatsHelper.getTodayUsageTime() / 1000
                val dataStr = """
                          {
                            "topApps": "$topApps",
                            "totalUsageSec": $totalSec
                          }
                          """.trimIndent()
                val usageDataPrefix = "Sulla base di questi dati registrati su questo dispositivo: $dataStr voglio che "
                val prompt = buildString {
                    append(usageDataPrefix)
                    append("generi $description. ")
                    append("La voglio IN QUESTO SPECIFICO FORMATO miraccomando: ")
                    append("DOMANDA==<testo>||OPZIONI==<3 opzioni BREVI separate da ';;'>||CORRETTA==<indice che parte da 0>")
                }

                Log.d("GameViewModel", "Prompt: $prompt")

                val raw = hfService.generateChat(
                    model = "deepseek/deepseek-prover-v2-671b", userPrompt = prompt
                )

                val (question, options, correct) = parseStructured(raw)
                pendingGameEvent = GameEvent(
                    lobbyId = game.value!!.lobbyId,
                    gameId = game.value!!.id,
                    senderUserId = player.value!!.userId,
                    recipientUserId = null,
                    eventType = eventType,
                    value = points,
                    createdAt = Instant.now().toString()
                )
                _dialog.value = GameDialogData.Question(
                    title = title, prompt = question, options = options, correctIndex = correct
                )
            } finally {
                _isLoadingQuestion.value = false
            }
        }
    }

    private fun parseStructured(raw: String): Triple<String, List<String>, Int> {
        val parts = raw.split("||").map { it.trim() }
        val question = parts[0].substringAfter("DOMANDA==").trim()
        val options = parts[1].substringAfter("OPZIONI==").split(";;").map { it.trim() }
        val correct = parts[2].substringAfter("CORRETTA==").toIntOrNull() ?: 0
        return Triple(question, options, correct)
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

    fun movePlayer(context: Context) {
        viewModelScope.launch {
            if (game.value == null || player.value == null) return@launch
            _players.value.firstOrNull { it.userId == player.value!!.userId }?.let { me ->
                val newPos = computeNewPosition(me.cellPosition, _diceRoll.value ?: 0)
                Log.d("GameViewModel", "New position: $newPos")
                gameRepository.updatePlayerPosition(newPos)
                _players.value = _players.value.map {
                    if (it.userId == me.userId) it.copy(cellPosition = newPos) else it
                }
                handleLanding(context, PERIMETER_CELLS[newPos]?.type)
            }
            gameState.value = GameState.END_TURN
        }
    }

    fun onDialogOptionSelected(idx: Int) {
        viewModelScope.launch {
            (dialog.value as? GameDialogData.Question)?.let { q ->
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
