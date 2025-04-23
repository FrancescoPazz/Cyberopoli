package com.unibo.cyberopoli.data.models.auth

import com.google.firebase.Timestamp

data class UserData(
    val userId: String? = null,
    val email: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val level: Int? = 0,
    val score: Int? = 0,
    val creationDate: Timestamp? = null,
    val profileImageUrl: String? = null,
    val totalGames: Int? = 0,
    val totalWins: Int? = 0,
    val totalMedals: Int? = 0,
    val bestScore: Int? = 0,
    val averageScore: Int? = 0
)