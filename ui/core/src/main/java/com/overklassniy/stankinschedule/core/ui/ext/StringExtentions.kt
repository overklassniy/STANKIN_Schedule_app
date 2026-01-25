package com.overklassniy.stankinschedule.core.ui.ext

import java.util.Locale

fun String.toTitleCase(locale: Locale = Locale.ROOT): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}