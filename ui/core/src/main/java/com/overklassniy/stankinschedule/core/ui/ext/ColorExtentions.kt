package com.overklassniy.stankinschedule.core.ui.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt


/**
 * Парсит HEX-строку в [Color].
 *
 * @param hex Строка HEX (например, "#80DEEA").
 * @return Цвет [Color].
 */
fun Color.Companion.parse(hex: String): Color {
    return Color(hex.toColorInt())
}

/**
 * Преобразует [Color] в строку HEX без альфа-канала.
 *
 * @return Строка вида "#RRGGBB".
 */
fun Color.toHEX(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}