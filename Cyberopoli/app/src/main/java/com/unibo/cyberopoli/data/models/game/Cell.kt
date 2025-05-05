package com.unibo.cyberopoli.data.models.game

data class Cell(
    val id: String,
    val type: CellType,
    val title: String,
    val description: String,
    val points: Int = 0
)
