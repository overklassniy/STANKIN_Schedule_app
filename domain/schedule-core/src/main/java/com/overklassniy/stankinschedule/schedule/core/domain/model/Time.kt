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
        // Миграция старых времён звонков (до версии 3.3.1) к актуальным значениям.
        // Раздельные карты для начал и окончаний, так как значение "15:50" является
        // одновременно новым временем начала (из старого "16:00") и старым временем окончания.
        val migratedStart = LEGACY_STARTS_MAP[startTime] ?: startTime
        val migratedEnd = LEGACY_ENDS_MAP[endTime] ?: endTime
        start = LocalTime.parse(migratedStart, formatter)
        end = LocalTime.parse(migratedEnd, formatter)
        if (!STARTS.contains(start.toString(TIME_PATTERN))
            || !ENDS.contains(end.toString(TIME_PATTERN))
        ) {
            throw IllegalArgumentException("Not parse time: $startTime - $endTime")
        }
        duration = ENDS.indexOf(migratedEnd) - STARTS.indexOf(migratedStart) + 1
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
         * @param time Строка времени (например, "8:30-10:05").
         * @return Новый экземпляр [Time].
         */
        fun fromString(time: String): Time {
            val (start, end) = time.split('-')
            return Time(start, end)
        }

        /** Список допустимых времен начала пар. */
        val STARTS =
            listOf("8:30", "10:15", "12:20", "14:05", "15:50", "18:00", "19:40", "21:20")

        /** Список допустимых времен окончания пар. */
        val ENDS =
            listOf("10:05", "11:50", "13:55", "15:40", "17:25", "19:30", "21:10", "22:50")

        /**
         * Карта миграции старых времён начала пар (до версии 3.3.1) к актуальным значениям.
         *
         * Используется для обратной совместимости при чтении данных из БД и JSON,
         * созданных в предыдущих версиях приложения.
         */
        private val LEGACY_STARTS_MAP: Map<String, String> = mapOf(
            "10:20" to "10:15",
            "14:10" to "14:05",
            "16:00" to "15:50",
        )

        /**
         * Карта миграции старых времён окончания пар (до версии 3.3.1) к актуальным значениям.
         *
         * Разделена от [LEGACY_STARTS_MAP], так как значение "15:50" является одновременно
         * новым временем начала (из старого "16:00") и старым временем окончания (→ "15:40").
         */
        private val LEGACY_ENDS_MAP: Map<String, String> = mapOf(
            "10:10" to "10:05",
            "12:00" to "11:50",
            "14:00" to "13:55",
            "15:50" to "15:40",
            "17:40" to "17:25",
        )
    }
}
