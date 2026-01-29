package com.overklassniy.stankinschedule.schedule.core.domain.exceptions

import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

/**
 * Базовый класс исключений, связанных с парами.
 */
open class PairException(
    message: String?, cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Исключение, возникающее при пересечении пар по времени и месту.
 */
class PairIntersectException(
    message: String?, val first: PairModel, val second: PairModel, cause: Throwable? = null,
) : PairException(message, cause)

/**
 * Базовый класс исключений, связанных с датами.
 */
sealed class DateException(
    message: String?, cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Исключение, связанное с некорректной периодичностью дат.
 */
class DateFrequencyException(
    message: String?, val date: String, val frequency: Frequency, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Исключение, возникающее при несовпадении дня недели (например, добавление даты вторника в модель для понедельника).
 */
class DateDayOfWeekException(
    message: String?, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Исключение ошибки парсинга даты.
 */
class DateParseException(
    message: String?, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Исключение пересечения дат.
 */
class DateIntersectException(
    message: String?, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Исключение пустого списка дат (когда ожидается непустой).
 */
class DateEmptyException(
    message: String? = null, cause: Throwable? = null
) : DateException(message, cause)