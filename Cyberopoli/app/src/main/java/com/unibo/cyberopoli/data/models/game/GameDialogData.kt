package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.Serializable

@Serializable
data class QuestionPayload(
    val title: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val eventType: String
)

sealed class GameDialogData {
    data class Question(
        val title: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int
    ) : GameDialogData()

    data class Result(
        val title: String,
        val message: String
    ) : GameDialogData()
}
