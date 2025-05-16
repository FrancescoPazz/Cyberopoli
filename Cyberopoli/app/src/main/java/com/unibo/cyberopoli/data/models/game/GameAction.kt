package com.unibo.cyberopoli.data.models.game

data class GameAction(
    val id: String,
    val iconRes: Int?,
    val action: () -> Unit
)