package com.unibo.cyberopoli.data.repositories.ranking

import com.unibo.cyberopoli.data.models.auth.User

interface IRankingRepository {
    suspend fun loadRanking() : List<User>?
}
