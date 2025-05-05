package com.unibo.cyberopoli.ui.screens.game.composables

import com.unibo.cyberopoli.ui.screens.game.Cell
import com.unibo.cyberopoli.ui.screens.game.CellType

const val BOARD_ROWS = 5
const val BOARD_COLS = 5
const val BOARD_SIZE = BOARD_ROWS * BOARD_COLS

val PERIMETER_INDICES: List<Int> = buildList {
    for (c in 0 until BOARD_COLS) add(c)
    for (r in 1 until BOARD_ROWS - 1) add(r * BOARD_COLS + BOARD_COLS - 1)
    for (c in BOARD_COLS - 1 downTo 0) add((BOARD_ROWS - 1) * BOARD_COLS + c)
    for (r in BOARD_ROWS - 2 downTo 1) add(r * BOARD_COLS)
}

data class CellConfig(val index: Int, val cell: Cell)
val PERIMETER_CELL_CONFIGS: List<CellConfig> = listOf(
    CellConfig(0,  Cell("0", CellType.START,  "Start",  "", "", 0)),
    CellConfig(1,  Cell("1", CellType.WHATSAPP, "WhatsApp", "Perdi 5", "", -5)),
    CellConfig(2,  Cell("2", CellType.DISCORD,  "Discord",  "Bonus casuale", "", 7)),
    CellConfig(3,  Cell("3", CellType.TIKTOK,   "TikTok",   "Salto +2", "", 0)),
    CellConfig(4,  Cell("4", CellType.HACKER,   "Hacker",   "Ruba 5", "", 0)),
    CellConfig(5,  Cell("5", CellType.INSTAGRAM,   "Instagram",   "Guadagni +5", "", 0)),
    CellConfig(9,  Cell("9", CellType.CHANCE,   "Chance",   "Pesca carta", "", 0)),
    CellConfig(10, Cell("10", CellType.SNAPCHAT,"Snapchat", "Perdi 3", "", -3)),
    CellConfig(14, Cell("14", CellType.INSTAGRAM, "Instagram", "Guadagni +5", "", 5)),
    CellConfig(15, Cell("15", CellType.CHANCE,  "Chance",   "Pesca carta", "", 0)),
    CellConfig(19, Cell("19", CellType.TELEGRAM, "Telegram", "Salto indietro", "", 0)),
    CellConfig(20, Cell("20", CellType.YOUTUBE, "YouTube",  "Guadagni +10", "", 10)),
    CellConfig(21, Cell("21", CellType.FACEBOOK,"Facebook", "Perdi 2", "", -2)),
    CellConfig(22, Cell("22", CellType.TIKTOK,  "TikTok",   "Salto +1", "", 0)),
    CellConfig(23, Cell("23", CellType.WHATSAPP,"WhatsApp", "Perdi 5", "", -5)),
    CellConfig(24, Cell("24", CellType.HACKER,  "Hacker",   "Ruba 5", "", 0))
)

fun createBoardCells(): List<Cell> {
    val map = PERIMETER_CELL_CONFIGS.associateBy { it.index }
    return List(BOARD_SIZE) { idx ->
        map[idx]?.cell ?: Cell(idx.toString(), CellType.COMMON, "", "", "", 0)
    }
}
