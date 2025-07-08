package com.unibo.cyberopoli.data.models.game.questions

import android.app.Application
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameDialogData

fun hackerStatements() = listOf(
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_1,
        contentRes = R.string.hacker_statement_content_1,
        points = 4,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_2,
        contentRes = R.string.hacker_statement_content_2,
        points = 5,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_3,
        contentRes = R.string.hacker_statement_content_3,
        points = 7,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_4,
        contentRes = R.string.hacker_statement_content_4,
        points = 5,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_5,
        contentRes = R.string.hacker_statement_content_5,
        points = 3,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_6,
        contentRes = R.string.hacker_statement_content_6,
        points = 5,
    ),
    GameDialogData.HackerStatement(
        titleRes = R.string.hacker_statement_title_7,
        contentRes = R.string.hacker_statement_content_7,
        points = 6,
    ),
)
