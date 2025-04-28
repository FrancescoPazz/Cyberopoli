package com.unibo.cyberopoli.domain.model

data class MatchPlayer(
    val user: User,
    val startScore: Int,
    val endScore: Int,
    val place: Int? = null
)
