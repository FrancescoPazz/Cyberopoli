package com.unibo.cyberopoli.data.models.game

const val BOARD_ROWS = 5
const val BOARD_COLS = 5
const val BOARD_SIZE = BOARD_ROWS * BOARD_COLS

val PERIMETER_PATH: List<Int> = buildList {
    repeat(BOARD_COLS) { add(it) }
    for (r in 1 until BOARD_ROWS - 1) add(r * BOARD_COLS + BOARD_COLS - 1)
    for (c in BOARD_COLS - 1 downTo 0) add((BOARD_ROWS - 1) * BOARD_COLS + c)
    for (r in BOARD_ROWS - 2 downTo 1) add(r * BOARD_COLS)
}

val PERIMETER_CELLS: Map<Int, GameCell> = listOf(
    0 to GameCell("0", GameEventType.START, "Start", "Ogni volta che passi di qua guadagni 50 punti", 50),
    1 to GameCell("1", GameEventType.TIKTOK, "TikTok", "Salto +2", 0),
    2 to GameCell("2", GameEventType.DISCORD, "Discord", "Bonus 7", 7),
    3 to GameCell("3", GameEventType.WHATSAPP, "WhatsApp", "Perdi 5", -5),
    4 to GameCell("4", GameEventType.HACKER, "Hacker", "Ruba 5", 0),
    5 to GameCell("5", GameEventType.INSTAGRAM, "Instagram", "Guadagni +5", 0),
    9 to GameCell("9", GameEventType.CHANCE, "Chance", "Pesca carta", 0),
    10 to GameCell("10", GameEventType.SNAPCHAT, "Snapchat", "Perdi 3", -3),
    14 to GameCell("14", GameEventType.TWITCH, "Twitch", "Guadagni +7", 7),
    15 to GameCell("15", GameEventType.CHANCE, "Chance", "Pesca carta", 0),
    19 to GameCell("19", GameEventType.TELEGRAM, "Telegram", "Salto indietro", 0),
    20 to GameCell("20", GameEventType.YOUTUBE, "YouTube", "Guadagni +10", 10),
    21 to GameCell("21", GameEventType.FACEBOOK, "Facebook", "Perdi 2", -2),
    22 to GameCell("22", GameEventType.REDDIT, "Reddit", "Salto +1", 0),
    23 to GameCell("23", GameEventType.LINKEDIN, "Linkedin", "Perdi 6", -6),
    24 to GameCell("24", GameEventType.HACKER, "Hacker", "Ruba 5", 0)
).toMap()

fun createBoard(): List<GameCell> = List(BOARD_SIZE) { idx ->
    PERIMETER_CELLS[idx] ?: GameCell(idx.toString(), GameEventType.COMMON, "", "", 0)
}