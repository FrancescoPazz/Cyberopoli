package com.unibo.cyberopoli.data.models.game

import kotlinx.serialization.Serializable

sealed class GameDialogData {
    @Serializable
    data class ChanceQuestion(
        val titleRes: Int,
        val promptRes: Int,
        val optionsRes: List<Int>,
        val correctIndex: Int,
        val points: Int,
    ) : GameDialogData()

    @Serializable
    data class HackerStatement(
        val titleRes: Int,
        val contentRes: Int,
        val points: Int,
    ) : GameDialogData()

    @Serializable
    data class BlockChoice(
        val titleRes: Int,
        val players: List<GamePlayer>,
    ) : GameDialogData()

    @Serializable
    data class SubscribeChoice(
        val titleRes: Int,
        val messageRes: Int,
        val messageArgs: List<String>? = null,
        val optionsRes: List<Int>,
        val cost: Int,
    ) : GameDialogData()

    @Serializable
    data class MakeContentChoice(
        val titleRes: Int,
        val messageRes: Int,
        val messageArgs: List<String>? = null,
        val optionsRes: List<Int>,
        val cost: Int,
    ) : GameDialogData()

    @Serializable
    data class Alert(
        val titleRes: Int,
        val messageRes: Int,
        val messageArgs: List<String>? = null,
        val optionsRes: List<Int>? = null,
    ) : GameDialogData()

    @Serializable
    data class QuestionResult(
        val titleRes: Int,
        val messageRes: Int,
        val messageArgs: List<String>? = null,
        val optionsRes: List<Int>,
        val correctIndex: Int,
        val selectedIndex: Int
    ) : GameDialogData()
}
