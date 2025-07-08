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

fun getAssetPositionFromPerimeterPosition(perimeterPosition: Int): Int? {
    return when (perimeterPosition) {
        // First line (top)
        in 8..12 -> perimeterPosition - BOARD_COLS

        // Last line (bottom)
        in 36..40 -> perimeterPosition + BOARD_COLS

        // Left board
        15, 22, 29 -> perimeterPosition - 1

        // Right board
        19, 26, 33 -> perimeterPosition + 1

        else -> null
    }
}

val PERIMETER_CELLS: Map<Int, GameCell> = listOf(
    // First line
    8 to GameCell("8", GameTypeCell.START, "Start", 50),
    9 to GameCell("9", GameTypeCell.TIKTOK, "TikTok", 12),
    10 to GameCell("10", GameTypeCell.VPN, "VPN"),
    11 to GameCell("11", GameTypeCell.WHATSAPP, "WhatsApp", 15),
    12 to GameCell("12", GameTypeCell.HACKER, "Hacker"),
    // Left board
    15 to GameCell("15", GameTypeCell.INSTAGRAM, "Instagram", 11),
    22 to GameCell("22", GameTypeCell.CHANCE, "Chance"),
    29 to GameCell("29", GameTypeCell.YOUTUBE, "YouTube", 10),
    // Right board
    19 to GameCell("19", GameTypeCell.TWITCH, "Twitch", 17),
    26 to GameCell("26", GameTypeCell.CHANCE, "Chance"),
    33 to GameCell("33", GameTypeCell.TELEGRAM, "Telegram", 14),
    // Last line
    36 to GameCell("36", GameTypeCell.HACKER, "Hacker"),
    37 to GameCell("37", GameTypeCell.FACEBOOK, "Facebook", 13),
    38 to GameCell("38", GameTypeCell.BLOCK, "Block"),
    39 to GameCell("39", GameTypeCell.DISCORD, "Discord", 17),
    40 to GameCell("40", GameTypeCell.BROKEN_ROUTER, "Broken Router"),
).toMap()

fun createBoard(): List<GameCell> = List(BOARD_SIZE) { idx ->
    PERIMETER_CELLS[idx] ?: GameCell(idx.toString(), GameTypeCell.COMMON, "Common")
}
