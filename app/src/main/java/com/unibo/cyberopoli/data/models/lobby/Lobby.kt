package com.unibo.cyberopoli.data.models.lobby

data class Lobby(
    val matchId: String = "",
    val hostId: String = "",
    val status: String = "waiting",
    val createdAt: Long = System.currentTimeMillis(),
    val players: Map<String, PlayerInfo> = mapOf()
)