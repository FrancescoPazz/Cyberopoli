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

val PERIMETER_CELLS: Map<Int, Cell> = listOf(
    0  to Cell("0", CellType.START, "Start", "Ogni volta che passi di qua guadagni 50 punti", 50),
    1  to Cell("1", CellType.TIKTOK,   "TikTok",   "Salto +2", 0),
    2  to Cell("2", CellType.DISCORD,  "Discord",  "Bonus 7", 7),
    3  to Cell("3", CellType.WHATSAPP, "WhatsApp", "Perdi 5", -5),
    4  to Cell("4", CellType.HACKER,   "Hacker",   "Ruba 5", 0),
    5  to Cell("5", CellType.INSTAGRAM,   "Instagram",   "Guadagni +5", 0),
    9  to Cell("9", CellType.CHANCE,   "Chance",   "Pesca carta", 0),
    10 to Cell("10", CellType.SNAPCHAT,"Snapchat", "Perdi 3", -3),
    14 to Cell("14", CellType.TWITCH, "Twitch", "Guadagni +7", 7),
    15 to Cell("15", CellType.CHANCE,  "Chance",   "Pesca carta", 0),
    19 to Cell("19", CellType.TELEGRAM, "Telegram", "Salto indietro", 0),
    20 to Cell("20", CellType.YOUTUBE, "YouTube",  "Guadagni +10", 10),
    21 to Cell("21", CellType.FACEBOOK,"Facebook", "Perdi 2", -2),
    22 to Cell("22", CellType.REDDIT,  "Reddit",   "Salto +1", 0),
    23 to Cell("23", CellType.LINKEDIN,"Linkedin", "Perdi 6", -6),
    24 to Cell("24", CellType.HACKER,  "Hacker",   "Ruba 5", 0)
).toMap()

fun createBoard(): List<Cell> = List(BOARD_SIZE) { idx ->
    PERIMETER_CELLS[idx] ?: Cell(idx.toString(), CellType.COMMON, "", "", 0)
}