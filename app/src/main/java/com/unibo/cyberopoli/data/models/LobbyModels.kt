package com.unibo.cyberopoli.data.models

data class Lobby(
    val matchId: String = "",
    val hostId: String = "",
    val status: String = "waiting",
    val createdAt: Long = System.currentTimeMillis(),
    val players: Map<String, PlayerInfo> = mapOf()
)

data class PlayerInfo(
    val name: String = "", var ready: Boolean = false, var score: Int = 50, var win: Boolean = false
)
