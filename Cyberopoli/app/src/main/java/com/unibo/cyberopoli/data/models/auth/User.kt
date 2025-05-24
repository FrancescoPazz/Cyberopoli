package com.unibo.cyberopoli.data.models.auth

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String? = null,
    @SerialName("surname") val surname: String? = null,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String? = null,
    @SerialName("is_guest") val isGuest: Boolean = false,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("level") val level: Int = 1,
    @SerialName("total_score") val totalScore: Int = 0,
    @SerialName("total_games") val totalGames: Int = 0,
    @SerialName("total_wins") val totalWins: Int = 0,
    @SerialName("total_medals") val totalMedals: Int = 0,
    @SerialName("created_at") val createdAt: String = Clock.System.now().toString(),
)
