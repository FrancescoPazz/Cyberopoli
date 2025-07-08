package com.unibo.cyberopoli.data.models.game.questions

import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameDialogData

fun chanceQuestions() = listOf(
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_1,
        promptRes = R.string.chance_question_prompt_1,
        optionsRes = listOf(
            R.string.chance_question_option_1_1,
            R.string.chance_question_option_1_2,
            R.string.chance_question_option_1_3,
            R.string.chance_question_option_1_4,
        ),
        correctIndex = 1,
        points = 7,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_2,
        promptRes = R.string.chance_question_prompt_2,
        optionsRes = listOf(
            R.string.chance_question_option_2_1,
            R.string.chance_question_option_2_2,
            R.string.chance_question_option_2_3,
            R.string.chance_question_option_2_4,
        ),
        correctIndex = 0,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_3,
        promptRes = R.string.chance_question_prompt_3,
        optionsRes = listOf(
            R.string.chance_question_option_3_1,
            R.string.chance_question_option_3_2,
            R.string.chance_question_option_3_3,
            R.string.chance_question_option_3_4,
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_4,
        promptRes = R.string.chance_question_prompt_4,
        optionsRes = listOf(
            R.string.chance_question_option_4_1,
            R.string.chance_question_option_4_2,
            R.string.chance_question_option_4_3,
            R.string.chance_question_option_4_4,
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_5,
        promptRes = R.string.chance_question_prompt_5,
        optionsRes = listOf(
            R.string.chance_question_option_5_1,
            R.string.chance_question_option_5_2,
            R.string.chance_question_option_5_3,
            R.string.chance_question_option_5_4,
        ),
        correctIndex = 1,
        points = 5,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_6,
        promptRes = R.string.chance_question_prompt_6,
        optionsRes = listOf(
            R.string.chance_question_option_6_1,
            R.string.chance_question_option_6_2,
            R.string.chance_question_option_6_3,
            R.string.chance_question_option_6_4,
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_7,
        promptRes = R.string.chance_question_prompt_7,
        optionsRes = listOf(
            R.string.chance_question_option_7_1,
            R.string.chance_question_option_7_2,
            R.string.chance_question_option_7_3,
            R.string.chance_question_option_7_4,
        ),
        correctIndex = 1,
        points = 4,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_8,
        promptRes = R.string.chance_question_prompt_8,
        optionsRes = listOf(
            R.string.chance_question_option_8_1,
            R.string.chance_question_option_8_2,
            R.string.chance_question_option_8_3,
            R.string.chance_question_option_8_4,
        ),
        correctIndex = 1,
        points = 3,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_9,
        promptRes = R.string.chance_question_prompt_9,
        optionsRes = listOf(
            R.string.chance_question_option_9_1,
            R.string.chance_question_option_9_2,
            R.string.chance_question_option_9_3,
            R.string.chance_question_option_9_4,
        ),
        correctIndex = 2,
        points = 4,
    ),
    GameDialogData.ChanceQuestion(
        titleRes = R.string.chance_question_title_10,
        promptRes = R.string.chance_question_prompt_10,
        optionsRes = listOf(
            R.string.chance_question_option_10_1,
            R.string.chance_question_option_10_2,
            R.string.chance_question_option_10_3,
            R.string.chance_question_option_10_4,
        ),
        correctIndex = 1,
        points = 8,
    ),
)