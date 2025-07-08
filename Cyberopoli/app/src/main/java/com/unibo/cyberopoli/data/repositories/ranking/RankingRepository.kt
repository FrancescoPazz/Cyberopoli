package com.unibo.cyberopoli.data.repositories.ranking

import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import com.unibo.cyberopoli.data.repositories.ranking.IRankingRepository as DomainUserRepository

class RankingRepository(
    private val supabase: SupabaseClient,
) : DomainUserRepository {
    val rankingUsersLiveData = MutableLiveData<List<User>?>()

    override suspend fun loadRanking() {
        try {
            val users: List<User> = supabase.from("users").select {
                order("total_score", order = Order.DESCENDING)
            }.decodeList<User>()
            rankingUsersLiveData.postValue(users)
        } catch (e: Exception) {
            throw e
        }
    }
}
