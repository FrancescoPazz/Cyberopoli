package com.unibo.cyberopoli.data.repositories.ranking

interface IRankingRepository {
    suspend fun loadRanking()
}