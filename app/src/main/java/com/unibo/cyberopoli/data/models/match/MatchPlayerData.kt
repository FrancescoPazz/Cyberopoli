package com.unibo.cyberopoli.data.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchPlayerData(
    @SerialName("id") val id: String,

    @SerialName("display_name") val displayName: String,

    @SerialName("score") val score: Int,

    @SerialName("place") val place: Int? = null
)
