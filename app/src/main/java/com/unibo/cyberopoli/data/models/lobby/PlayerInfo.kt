package com.unibo.cyberopoli.data.models.lobby

data class PlayerInfo(
    val name: String = "", var ready: Boolean = false, var score: Int = 50, var win: Boolean = false
)