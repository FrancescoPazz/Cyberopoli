package com.unibo.cyberopoli.data.models.game.questions

import android.app.Application
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GameDialogData

fun hackerStatements(app: Application) = listOf(
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_1),
        content = app.getString(R.string.hacker_statement_content_1),
        points = 4,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_2),
        content = app.getString(R.string.hacker_statement_content_2),
        points = 5,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_3),
        content = app.getString(R.string.hacker_statement_content_3),
        points = 7,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_4),
        content = app.getString(R.string.hacker_statement_content_4),
        points = 5,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_5),
        content = app.getString(R.string.hacker_statement_content_5),
        points = 3,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_6),
        content = app.getString(R.string.hacker_statement_content_6),
        points = 5,
    ),
    GameDialogData.HackerStatement(
        title = app.getString(R.string.hacker_statement_title_7),
        content = app.getString(R.string.hacker_statement_content_7),
        points = 6,
    ),
)
