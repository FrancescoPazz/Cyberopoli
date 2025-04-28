package com.unibo.cyberopoli.data.repositories

import com.unibo.cyberopoli.data.models.match.MatchPlayerData
import com.unibo.cyberopoli.ui.screens.match.Match
import io.github.jan.supabase.SupabaseClient

class MatchRepository(
    private val supabase: SupabaseClient
) {
    suspend fun createMatch(lobbyId: String): Match {
        // Qui chiami Supabase o il tuo backend per creare la partita
        TODO()
    }

    suspend fun getMatchPlayers(matchId: String): List<MatchPlayerData> {
        // Fetch dei giocatori con punti iniziali
        TODO()
    }

    suspend fun addPointEvent(matchId: String, userId: String, delta: Int) {
        // Aggiunge un evento ai punti
        TODO()
    }
}
