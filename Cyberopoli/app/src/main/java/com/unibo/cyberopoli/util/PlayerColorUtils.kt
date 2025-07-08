package com.unibo.cyberopoli.util

import androidx.compose.ui.graphics.Color
import com.unibo.cyberopoli.data.models.game.GamePlayer

private const val COLOR_PURPLE = 0xFF6200EA
private const val COLOR_RED = 0xFFD50000
private const val COLOR_GREEN = 0xFF00C853
private const val COLOR_YELLOW = 0xFFFFAB00
private const val COLOR_BLUE = 0xFF2962FF

object PlayerColorUtils {
    private val playerColors = mutableMapOf<String, Color>()

    private val predefinedColors = listOf(
        Color(COLOR_PURPLE), // purple_pawn
        Color(COLOR_RED), // red_pawn
        Color(COLOR_GREEN), // green_pawn
        Color(COLOR_YELLOW), // yellow_pawn
        Color(COLOR_BLUE), // blue_pawn
    )

    fun getPawnModelForPlayer(player: GamePlayer): String {
        val color = getPlayerColor(player)
        return when (color) {
            Color(COLOR_PURPLE) -> "models/purple_pawn.glb"
            Color(COLOR_RED) -> "models/red_pawn.glb"
            Color(COLOR_GREEN) -> "models/green_pawn.glb"
            Color(COLOR_YELLOW) -> "models/yellow_pawn.glb"
            Color(COLOR_BLUE) -> "models/blue_pawn.glb"
            else -> "models/chess_pawn.glb" // default
        }
    }

    private val shuffledColors = predefinedColors.shuffled()

    fun getPlayerColor(player: GamePlayer): Color {
        return playerColors.getOrPut(player.userId) {
            val colorIndex = playerColors.size % shuffledColors.size
            shuffledColors[colorIndex]
        }
    }


}