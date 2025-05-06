package com.unibo.cyberopoli.data.repositories.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankingRepository(
    private val supabase: SupabaseClient
) {
    val rankingLiveData = MutableLiveData<List<User>>()

    fun loadRanking() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users: List<User> = supabase.from("users").select {
                    order("total_score", order = Order.DESCENDING)
                }.decodeList<User>()

                rankingLiveData.postValue(users)
            } catch (e: Exception) {
                Log.e("RankingRepository", "loadRanking error: ${e.message}")
                rankingLiveData.postValue(emptyList())
            }
        }
    }
}
