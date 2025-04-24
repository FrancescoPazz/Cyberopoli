package com.unibo.cyberopoli.data.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("is_guest")
    val isGuest: Boolean = false,

    @SerialName("name")
    val name: String,

    @SerialName("surname")
    val surname: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("level")
    val level: Int = 1,

    @SerialName("total_score")
    val score: Int = 0,

    @SerialName("total_games")
    val totalGames: Int = 0,

    @SerialName("total_wins")
    val totalWins: Int = 0,

    @SerialName("total_medals")
    val totalMedals: Int = 0,

    @SerialName("best_score")
    val bestScore: Int = 0,

    @SerialName("average_score")
    val averageScore: Int = 0,

    @SerialName("created_at")
    val createdAt: String? = null,
)