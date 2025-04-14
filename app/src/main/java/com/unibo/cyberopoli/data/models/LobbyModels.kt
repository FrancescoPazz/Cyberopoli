package com.unibo.cyberopoli.data.models

data class Lobby(
    val hostId: String = "",
    val status: String = "waiting",
    val createdAt: Long = System.currentTimeMillis(),
    val gameSettings: Map<String, Any>? = null,
    val players: Map<String, PlayerInfo> = mapOf()
)

data class PlayerInfo(
    val name: String = "",
    val ready: Boolean = false,
    val score: Int = 50
)
