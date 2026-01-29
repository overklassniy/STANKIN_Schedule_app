package com.overklassniy.stankinschedule.schedule.parser.domain.model

/**
 * Представляет границы текстовой ячейки в PDF документе.
 *
 * @property text Текст внутри ячейки.
 * @property x Координата X левого края ячейки.
 * @property y Координата Y верхнего края ячейки.
 * @property h Высота ячейки.
 * @property w Ширина ячейки.
 * @property maxFontHeight Максимальная высота шрифта в ячейке.
 */
class CellBound(
    val text: String,
    val x: Float,
    val y: Float,
    val h: Float,
    val w: Float,
    val maxFontHeight: Float
) {
    override fun toString(): String {
        return "CellBound(text='$text', x=$x, y=$y, h=$h, w=$w, maxFontHeight=$maxFontHeight)"
    }
}