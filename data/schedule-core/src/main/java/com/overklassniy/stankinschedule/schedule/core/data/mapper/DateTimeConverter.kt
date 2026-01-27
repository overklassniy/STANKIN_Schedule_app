package com.overklassniy.stankinschedule.schedule.core.data.mapper

import androidx.room.TypeConverter
import org.joda.time.DateTime

/**
 * Конвертер даты для Room DB.
 */
class DateTimeConverter {

    /**
     * Конвертирует объект [DateTime] в строку.
     *
     * @param dateTime Объект даты и времени (может быть null).
     * @return Строковое представление даты и времени или null.
     */
    @Suppress("unused")
    @TypeConverter
    fun fromDateTime(dateTime: DateTime?): String? {
        return dateTime?.toString()
    }

    /**
     * Конвертирует строку в объект [DateTime].
     *
     * @param dateTime Строковое представление даты и времени (может быть null).
     * @return Объект [DateTime] или null.
     */
    @Suppress("unused")
    @TypeConverter
    fun toDateTime(dateTime: String?): DateTime? {
        return dateTime?.let { DateTime.parse(it) }
    }
}