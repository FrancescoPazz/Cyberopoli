package com.unibo.cyberopoli.data.models.game

data class GameCell(
    val id: String,
    val type: GameTypeCell,
    val title: String,
    val value: Int? = 0,
)
