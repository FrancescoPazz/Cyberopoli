package com.unibo.cyberopoli.data.models

data class RankingUser(
    val userId: String,
    val rank: Int,
    val name: String,
    val surname: String,
    val score: Int,
    val avatarUrl: String
)
