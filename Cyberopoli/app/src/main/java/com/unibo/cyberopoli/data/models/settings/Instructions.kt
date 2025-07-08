package com.unibo.cyberopoli.data.models.settings

import android.content.Context
import com.unibo.cyberopoli.R

fun CyberopoliInstructions(context: Context) = listOf(
    Pair(
        context.getString(R.string.instruction_game_title),
        context.getString(R.string.instruction_game_description)
    ), Pair(
        context.getString(R.string.instruction_access_title),
        context.getString(R.string.instruction_access_description)
    ), Pair(
        context.getString(R.string.instruction_lobby_title),
        context.getString(R.string.instruction_lobby_description)
    ), Pair(
        context.getString(R.string.instruction_play_title),
        context.getString(R.string.instruction_play_description)
    ), Pair(
        context.getString(R.string.instruction_tile_title),
        context.getString(R.string.instruction_tile_description)
    ), Pair(
        context.getString(R.string.instruction_goal_title),
        context.getString(R.string.instruction_goal_description)
    ), Pair(
        context.getString(R.string.instruction_end_title),
        context.getString(R.string.instruction_end_description)
    ), Pair(
        context.getString(R.string.instruction_account_title),
        context.getString(R.string.instruction_account_description)
    ), Pair(
        context.getString(R.string.instruction_tips_title),
        context.getString(R.string.instruction_tips_description)
    )
)
