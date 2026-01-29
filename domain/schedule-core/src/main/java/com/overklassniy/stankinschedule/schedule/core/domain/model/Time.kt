package com.overklassniy.stankinschedule.schedule.core.domain.model

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * Класс, представляющий время проведения пары (начало и конец).
 *
 * Также вычисляет продолжительность занятия в академических часах (парах).
 */
class Time(startTime: String, endTime: String) {

    /** Время начала пары. */
    val start: LocalTime

    /** Время окончания пары. */
    val end: LocalTime

    /** Продолжительность в парах (обычно 1, но может быть больше для сдвоенных занятий). */
    val duration: Int

    init {
        val formatter = DateTimeFormat.forPattern(TIME_PATTERN)
        start = LocalTime.parse(startTime, formatter)
        end = LocalTime.parse(endTime, formatter)
        if (!STARTS.contains(start.toString(TIME_PATTERN))
            || !ENDS.contains(end.toString(TIME_PATTERN))
        ) {
            throw IllegalArgumentException("Not parse time: $startTime - $endTime")
        }
        duration = ENDS.indexOf(endTime) - STARTS.indexOf(startTime) + 1
    }

    /**
     * Проверяет пересечение временных интервалов.
     *
     * @param other Другое время.
     * @return true, если интервалы пересекаются.
     */
    fun isIntersect(other: Time): Boolean {
        return (start >= other.start && end <= other.end) ||
                (start <= other.start && end >= other.end) ||
                (start <= other.end && end >= other.end)
    }

    /**
     * Возвращает время начала в строковом формате (H:mm).
     */
    fun startString(): String = start.toString(TIME_PATTERN)

    /**
     * Возвращает время окончания в строковом формате (H:mm).
     */
    fun endString(): String = end.toString(TIME_PATTERN)

    /**
     * Возвращает порядковый номер пары (начиная с 0).
     */
    fun number(): Int {
        return STARTS.indexOf(start.toString(TIME_PATTERN))
    }

    /**
     * Проверяет равенство с другим временем.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Time

        if (start != other.start) return false
        if (end != other.end) return false
        if (duration != other.duration) return false

        return true
    }

    /**
     * Возвращает хэш-код.
     */
    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + duration
        return result
    }

    /**
     * Возвращает строку времени в формате H:mm-H:mm.
     */
    override fun toString(): String {
        return "${start.toString(TIME_PATTERN)}-${end.toString(TIME_PATTERN)}"
    }

    companion object {
        private const val TIME_PATTERN = "H:mm"

        /**
         * Создает объект [Time] из строки формата "H:mm-H:mm".
         *
         * @param time Строка времени (например, "8:30-10:10").
         * @return Новый экземпляр [Time].
         */
        fun fromString(time: String): Time {
            val (start, end) = time.split('-')
            return Time(start, end)
        }

        /** Список допустимых времен начала пар. */
        val STARTS =
            listOf("8:30", "10:20", "12:20", "14:10", "16:00", "18:00", "19:40", "21:20")

        /** Список допустимых времен окончания пар. */
        val ENDS =
            listOf("10:10", "12:00", "14:00", "15:50", "17:40", "19:30", "21:10", "22:50")
    }
}