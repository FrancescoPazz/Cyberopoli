package com.unibo.cyberopoli.data.models.game

data class GameCell(
    val id: String,
    val type: GameEventType,
    val title: String,
    val description: String,
    val points: Int = 0
)
