package com.overklassniy.stankinschedule.core.ui.utils

import android.content.Context
import com.overklassniy.stankinschedule.core.ui.R

/**
 * Форматирует название семестра, полученное от API, в локализованную строку.
 *
 * API возвращает семестры в формате «YYYY-осень» или «YYYY-весна».
 * Русская часть заменяется на локализованное название времени года.
 * Если формат не распознан, возвращается исходная строка без изменений.
 *
 * Алгоритм:
 * 1. Разделить строку по символу «-».
 * 2. Если частей две и вторая часть — известное время года, заменить её локализованной строкой.
 * 3. Собрать строку обратно.
 *
 * @param context Контекст для доступа к строковым ресурсам.
 * @param semester Исходная строка семестра от API.
 * @return Локализованное название семестра.
 */
fun formatSemesterName(
    context: Context,
    semester: String,
): String {
    val parts = semester.split("-")
    if (parts.size != 2) return semester

    val year = parts[0]
    val season = parts[1].lowercase()

    val localizedSeason = when (season) {
        "весна" -> context.getString(R.string.semester_spring)
        "осень" -> context.getString(R.string.semester_autumn)
        else -> return semester
    }

    return "$year $localizedSeason"
}
