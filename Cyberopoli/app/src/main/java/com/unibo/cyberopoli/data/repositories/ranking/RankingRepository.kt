package com.unibo.cyberopoli.data.repositories.ranking

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.postgrest.query.Order
import com.unibo.cyberopoli.data.repositories.auth.USERS_TABLE
import com.unibo.cyberopoli.data.repositories.ranking.IRankingRepository as DomainUserRepository

class RankingRepository(
    private val supabase: SupabaseClient,
) : DomainUserRepository {
    override suspend fun loadRanking() : List<User>? {
        return try {
            supabase.from(USERS_TABLE).select {
                order("total_score", order = Order.DESCENDING)
            }.decodeList<User>()
        } catch (e: Exception) {
            throw e
        }
    }
}
