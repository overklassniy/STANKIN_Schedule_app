package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateDayOfWeekException
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Перечисление дней недели.
 *
 * Используется для определения дня недели занятий.
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    companion object {

        /**
         * Определяет день недели по дате.
         *
         * @param date Дата [LocalDate].
         * @return Соответствующий [DayOfWeek].
         * @throws DateDayOfWeekException Если день недели не поддерживается (например, воскресенье).
         */
        fun of(date: LocalDate): DayOfWeek {
            return when (date.dayOfWeek) {
                DateTimeConstants.MONDAY -> MONDAY
                DateTimeConstants.TUESDAY -> TUESDAY
                DateTimeConstants.WEDNESDAY -> WEDNESDAY
                DateTimeConstants.THURSDAY -> THURSDAY
                DateTimeConstants.FRIDAY -> FRIDAY
                DateTimeConstants.SATURDAY -> SATURDAY
                else -> {
                    throw DateDayOfWeekException("Invalid day of week: $date")
                }
            }
        }
    }
}