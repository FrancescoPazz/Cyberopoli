package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.Serializable

sealed class GameDialogData {
    @Serializable
    data class ChanceQuestion(
        val title: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int,
        val points: Int
    ) : GameDialogData()

    @Serializable
    data class HackerQuestion(
        val title: String,
        val content: String,
        val points: Int
    ) : GameDialogData()

    @Serializable
    data class BlockChoice(
        val title: String,
        val players: List<GamePlayer>
    ) : GameDialogData()

    @Serializable
    data class SubscribeChoice(
        val title: String,
        val options: List<String>,
        val cost: Int,
    ) : GameDialogData()

    @Serializable
    data class Alert(
        val title: String,
        val message: String,
        val options: List<String>? = null,
        val onDismiss: (() -> Unit)? = null
    ) : GameDialogData()
}
