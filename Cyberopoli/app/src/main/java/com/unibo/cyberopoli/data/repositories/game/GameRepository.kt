package com.unibo.cyberopoli.data.repositories.game

import com.unibo.cyberopoli.data.models.game.Game
import com.unibo.cyberopoli.data.models.game.GamePlayer
import io.github.jan.supabase.SupabaseClient
import com.unibo.cyberopoli.data.repositories.game.IGameRepository as DomainMatchRepository

class GameRepository(
    private val supabase: SupabaseClient
) : DomainMatchRepository {
    override suspend fun createGame(lobbyId: String): Game {
        TODO()
    }

    override suspend fun getGamePlayers(matchId: String): List<GamePlayer> {
        TODO()
    }

    override suspend fun addPointEvent(matchId: String, userId: String, delta: Int) {
        TODO()
    }
}
