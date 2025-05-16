package com.unibo.cyberopoli.data.models.game

const val BOARD_ROWS = 7
const val BOARD_COLS = 7
const val BOARD_SIZE = BOARD_ROWS * BOARD_COLS

val PERIMETER_PATH: List<Int> = buildList {
    for (c in 1 until BOARD_COLS - 1) {
        add(1 * BOARD_COLS + c)
    }
    for (r in 2 until BOARD_ROWS - 2) {
        add(r * BOARD_COLS + (BOARD_COLS - 2))
    }
    for (c in BOARD_COLS - 2 downTo 1) {
        add((BOARD_ROWS - 2) * BOARD_COLS + c)
    }
    for (r in BOARD_ROWS - 3 downTo 2) {
        add(r * BOARD_COLS + 1)
    }
}


val PERIMETER_CELLS: Map<Int, GameCell> = listOf(
    8 to GameCell("8", GameTypeCell.START, "Start", 50),
    9 to GameCell("9", GameTypeCell.TIKTOK, "TikTok", 6),
    10 to GameCell("10", GameTypeCell.VPN, "VPN"),
    11 to GameCell("11", GameTypeCell.WHATSAPP, "WhatsApp", 5),
    12 to GameCell("12", GameTypeCell.HACKER, "Hacker"),
    15 to GameCell("15", GameTypeCell.INSTAGRAM, "Instagram", 0),
    19 to GameCell("19", GameTypeCell.TWITCH, "Twitch",  7),
    26 to GameCell("26", GameTypeCell.CHANCE, "Chance"),
    22 to GameCell("22", GameTypeCell.CHANCE, "Chance"),
    29 to GameCell("29", GameTypeCell.YOUTUBE, "YouTube", 10),
    33 to GameCell("33", GameTypeCell.TELEGRAM, "Telegram",  4),
    36 to GameCell("36", GameTypeCell.HACKER, "Hacker"),
    37 to GameCell("37", GameTypeCell.FACEBOOK, "Facebook", 3),
    38 to GameCell("38", GameTypeCell.BLOCK, "Block"),
    39 to GameCell("39", GameTypeCell.DISCORD, "Discord", 7),
    40 to GameCell("40", GameTypeCell.BROKEN_ROUTER, "Broken Router"),
).toMap()

fun createBoard(): List<GameCell> = List(BOARD_SIZE) { idx ->
    PERIMETER_CELLS[idx] ?: GameCell(idx.toString(), GameTypeCell.COMMON, "Common")
}