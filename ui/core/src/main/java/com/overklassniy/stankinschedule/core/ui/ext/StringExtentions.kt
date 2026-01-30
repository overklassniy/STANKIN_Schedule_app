package com.overklassniy.stankinschedule.core.ui.ext

import java.util.Locale

/**
 * Преобразует первую букву строки в верхний регистр с учётом [locale].
 *
 * @param locale Локаль для преобразования.
 * @return Строка с заглавной первой буквой.
 */
fun String.toTitleCase(locale: Locale = Locale.ROOT): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}