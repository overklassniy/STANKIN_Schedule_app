package com.overklassniy.stankinschedule.core.domain.ext

import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.LocalDate
import org.joda.time.Minutes
import java.util.Locale

/**
 * Вычисляет разницу в минутах между двумя моментами времени.
 *
 * Функция-инфикс.
 *
 * @receiver Начальный момент времени.
 * @param other Конечный момент времени.
 * @return Количество минут между [this] и [other].
 */
infix fun DateTime.subMinutes(other: DateTime): Int {
    return Minutes.minutesBetween(this, other).minutes
}

/**
 * Вычисляет разницу в часах между двумя моментами времени.
 *
 * Функция-инфикс.
 *
 * @receiver Начальный момент времени.
 * @param other Конечный момент времени.
 * @return Количество часов между [this] и [other].
 */
infix fun DateTime.subHours(other: DateTime): Int {
    return Hours.hoursBetween(this, other).hours
}

/**
 * Форматирует строковое представление даты в заданный формат.
 *
 * Парсит входную строку [date] как [LocalDate] и форматирует её в строку согласно [pattern].
 *
 * @param date Строка с датой для парсинга (ожидается формат, поддерживаемый [LocalDate.parse] по умолчанию, например "yyyy-MM-dd").
 * @param pattern Шаблон формата для вывода (по умолчанию "dd.MM.yyyy").
 * @param locale Локаль для форматирования (по умолчанию [Locale.ROOT]).
 * @return Отформатированная строка даты.
 */
fun formatDate(date: String, pattern: String = "dd.MM.yyyy", locale: Locale = Locale.ROOT): String {
    return LocalDate.parse(date).toString(pattern, locale)
}