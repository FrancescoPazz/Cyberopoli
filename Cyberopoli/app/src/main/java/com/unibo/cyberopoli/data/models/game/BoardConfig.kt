package com.unibo.cyberopoli.data.models.game

const val BOARD_ROWS = 7
const val BOARD_COLS = 7
const val BOARD_SIZE = BOARD_ROWS * BOARD_COLS

val PERIMETER_PATH: List<Int> = buildList {
    for (c in 1 until BOARD_COLS - 1) {
        add(1 * BOARD_COLS + c)
    }
    for (r in 2 until BOARD_ROWS - 1) {
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
    8 to GameCell("8", GameEventType.START, "Start", "Ogni volta che passi di qua guadagni 50 punti", 50),
    9 to GameCell("9", GameEventType.TIKTOK, "TikTok", "Salto +2", 0),
    10 to GameCell("10", GameEventType.VPN, "VPN", "", 0),
    11 to GameCell("11", GameEventType.WHATSAPP, "WhatsApp", "Perdi 5", -5),
    12 to GameCell("12", GameEventType.HACKER, "Hacker", "", 0),
    15 to GameCell("15", GameEventType.INSTAGRAM, "Instagram", "Guadagni +5", 0),
    19 to GameCell("19", GameEventType.TWITCH, "Twitch", "Guadagni +7", 7),
    26 to GameCell("26", GameEventType.CHANCE, "Chance", "", 0),
    22 to GameCell("22", GameEventType.CHANCE, "Chance", "", 0),
    29 to GameCell("29", GameEventType.YOUTUBE, "YouTube", "Guadagni +10", 10),
    33 to GameCell("33", GameEventType.TELEGRAM, "Telegram", "Salto indietro", 0),
    36 to GameCell("36", GameEventType.HACKER, "Hacker", "", 0),
    37 to GameCell("37", GameEventType.FACEBOOK, "Facebook", "Perdi 2", -2),
    38 to GameCell("38", GameEventType.BLOCK, "BLOCK", "", 0),
    39 to GameCell("39", GameEventType.DISCORD, "Discord", "Bonus 7", 7),
    40 to GameCell("40", GameEventType.BROKEN_ROUTER, "", "", 0),
).toMap()

fun createBoard(): List<GameCell> = List(BOARD_SIZE) { idx ->
    PERIMETER_CELLS[idx] ?: GameCell(idx.toString(), GameEventType.COMMON, "", "", 0)
}