package com.unibo.cyberopoli.data.models

data class UserData(
    val avatarUrl: String,
    val name: String,
    val role: String,
    val totalGames: Int,
    val totalWins: Int,
    val totalMedals: Int
)

data class MatchHistory(
    val date: String, val title: String, val result: String, val points: String
)
