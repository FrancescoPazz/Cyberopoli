package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.Serializable

sealed class GameDialogData {
    @Serializable
    data class ChanceQuestion(
        val title: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int,
    ) : GameDialogData()

    @Serializable
    data class HackerQuestion(
        val title: String,
        val content: String,
        val points: Int,
    ) : GameDialogData()

    @Serializable
    data class Result(
        val title: String,
        val message: String
    ) : GameDialogData()
}
