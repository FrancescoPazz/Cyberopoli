package com.unibo.cyberopoli.data.repositories.match

import com.unibo.cyberopoli.domain.model.MatchPlayer
import com.unibo.cyberopoli.ui.screens.match.Match
import io.github.jan.supabase.SupabaseClient
import com.unibo.cyberopoli.domain.repository.IMatchRepository as DomainMatchRepository

class MatchRepository(
    private val supabase: SupabaseClient
) : DomainMatchRepository {
    override suspend fun createMatch(lobbyId: String): Match {
        TODO()
    }

    override suspend fun getMatchPlayers(matchId: String): List<MatchPlayer> {
        TODO()
    }

    override suspend fun addPointEvent(matchId: String, userId: String, delta: Int) {
        TODO()
    }
}
