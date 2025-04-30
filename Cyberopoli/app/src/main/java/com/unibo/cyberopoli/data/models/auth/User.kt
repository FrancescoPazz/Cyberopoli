package com.unibo.cyberopoli.data.models.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String? = null,
    @SerialName("is_guest") val isGuest: Boolean = false,
    @SerialName("first_name") val name: String? = null,
    @SerialName("last_name") val surname: String? = null,
    @SerialName("display_name") val username: String = "$name $surname",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("level") val level: Int = 1,
    @SerialName("total_score") val totalScore: Int = 0,
    @SerialName("total_games") val totalGames: Int = 0,
    @SerialName("total_wins") val totalWins: Int = 0,
    @SerialName("total_medals") val totalMedals: Int = 0,
    @SerialName("created_at") val createdAt: String = Instant.now().toString(),
)
