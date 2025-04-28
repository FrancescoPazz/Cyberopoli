package com.unibo.cyberopoli.ui.screens.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.models.match.MatchPlayerData
import com.unibo.cyberopoli.data.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _match = MutableStateFlow<Match?>(null)
    val match = _match.asStateFlow()

    private val _players = MutableStateFlow<List<MatchPlayerData>>(emptyList())
    val players = _players.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex = _currentTurnIndex.asStateFlow()

    fun startMatch(lobbyId: String) {
        viewModelScope.launch {
            val newMatch = matchRepository.createMatch(lobbyId)
            _match.value = newMatch
            _players.value = matchRepository.getMatchPlayers(newMatch.id)
        }
    }

    fun nextTurn() {
        _currentTurnIndex.value = (_currentTurnIndex.value + 1) % _players.value.size
    }

    fun updatePlayerPoints(userId: String, delta: Int) {
        viewModelScope.launch {
            matchRepository.addPointEvent(matchId = _match.value!!.id, userId = userId, delta = delta)
            _players.value = matchRepository.getMatchPlayers(_match.value!!.id)
        }
    }
}
