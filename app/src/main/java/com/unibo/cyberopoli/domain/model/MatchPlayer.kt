package com.unibo.cyberopoli.domain.model

data class MatchPlayer(
    val user: User,
    val score: Int,
    val place: Int? = null
)
