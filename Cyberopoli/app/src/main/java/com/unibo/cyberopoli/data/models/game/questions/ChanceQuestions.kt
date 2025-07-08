package com.unibo.cyberopoli.data.models.game.questions

import android.app.Application
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameDialogData

fun chanceQuestions(app: Application) = listOf(
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_1),
        prompt = app.getString(R.string.chance_question_prompt_1),
        options = listOf(
            app.getString(R.string.chance_question_option_1_1),
            app.getString(R.string.chance_question_option_1_2),
            app.getString(R.string.chance_question_option_1_3),
            app.getString(R.string.chance_question_option_1_4),
        ),
        correctIndex = 1,
        points = 7,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_2),
        prompt = app.getString(R.string.chance_question_prompt_2),
        options = listOf(
            app.getString(R.string.chance_question_option_2_1),
            app.getString(R.string.chance_question_option_2_2),
            app.getString(R.string.chance_question_option_2_3),
            app.getString(R.string.chance_question_option_2_4),
        ),
        correctIndex = 0,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_3),
        prompt = app.getString(R.string.chance_question_prompt_3),
        options = listOf(
            app.getString(R.string.chance_question_option_3_1),
            app.getString(R.string.chance_question_option_3_2),
            app.getString(R.string.chance_question_option_3_3),
            app.getString(R.string.chance_question_option_3_4),
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_4),
        prompt = app.getString(R.string.chance_question_prompt_4),
        options = listOf(
            app.getString(R.string.chance_question_option_4_1),
            app.getString(R.string.chance_question_option_4_2),
            app.getString(R.string.chance_question_option_4_3),
            app.getString(R.string.chance_question_option_4_4),
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_5),
        prompt = app.getString(R.string.chance_question_prompt_5),
        options = listOf(
            app.getString(R.string.chance_question_option_5_1),
            app.getString(R.string.chance_question_option_5_2),
            app.getString(R.string.chance_question_option_5_3),
            app.getString(R.string.chance_question_option_5_4),
        ),
        correctIndex = 1,
        points = 5,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_6),
        prompt = app.getString(R.string.chance_question_prompt_6),
        options = listOf(
            app.getString(R.string.chance_question_option_6_1),
            app.getString(R.string.chance_question_option_6_2),
            app.getString(R.string.chance_question_option_6_3),
            app.getString(R.string.chance_question_option_6_4),
        ),
        correctIndex = 1,
        points = 6,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_7),
        prompt = app.getString(R.string.chance_question_prompt_7),
        options = listOf(
            app.getString(R.string.chance_question_option_7_1),
            app.getString(R.string.chance_question_option_7_2),
            app.getString(R.string.chance_question_option_7_3),
            app.getString(R.string.chance_question_option_7_4),
        ),
        correctIndex = 1,
        points = 4,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_8),
        prompt = app.getString(R.string.chance_question_prompt_8),
        options = listOf(
            app.getString(R.string.chance_question_option_8_1),
            app.getString(R.string.chance_question_option_8_2),
            app.getString(R.string.chance_question_option_8_3),
            app.getString(R.string.chance_question_option_8_4),
        ),
        correctIndex = 1,
        points = 3,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_9),
        prompt = app.getString(R.string.chance_question_prompt_9),
        options = listOf(
            app.getString(R.string.chance_question_option_9_1),
            app.getString(R.string.chance_question_option_9_2),
            app.getString(R.string.chance_question_option_9_3),
            app.getString(R.string.chance_question_option_9_4),
        ),
        correctIndex = 2,
        points = 4,
    ),
    GameDialogData.ChanceQuestion(
        title = app.getString(R.string.chance_question_title_10),
        prompt = app.getString(R.string.chance_question_prompt_10),
        options = listOf(
            app.getString(R.string.chance_question_option_10_1),
            app.getString(R.string.chance_question_option_10_2),
            app.getString(R.string.chance_question_option_10_3),
            app.getString(R.string.chance_question_option_10_4),
        ),
        correctIndex = 1,
        points = 8,
    ),
)
