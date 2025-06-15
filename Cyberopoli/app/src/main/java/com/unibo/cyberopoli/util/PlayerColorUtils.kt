package com.unibo.cyberopoli.util

import androidx.compose.ui.graphics.Color
import com.unibo.cyberopoli.data.models.game.GamePlayer
import kotlin.random.Random

object PlayerColorUtils {
    private val playerColors = mutableMapOf<String, Color>()

    private val predefinedColors = listOf(
        Color(0xFF6200EA),
        Color(0xFFD50000),
        Color(0xFF00C853),
        Color(0xFFFFAB00),
        Color(0xFF2962FF),
        Color(0xFFFF6D00),
        Color(0xFF00BFA5),
        Color(0xFF6D4C41),
    )

    fun getPlayerColor(player: GamePlayer): Color {
        return playerColors.getOrPut(player.userId) {
            if (playerColors.size < predefinedColors.size) {
                predefinedColors[playerColors.size]
            } else {
                Color(
                    red = Random.nextFloat() * 0.7f + 0.3f,
                    green = Random.nextFloat() * 0.7f + 0.3f,
                    blue = Random.nextFloat() * 0.7f + 0.3f
                )
            }
        }
    }
}