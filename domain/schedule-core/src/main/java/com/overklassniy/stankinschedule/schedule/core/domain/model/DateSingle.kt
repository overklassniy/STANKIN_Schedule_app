package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateDayOfWeekException
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateParseException
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Класс, представляющий одиночную дату занятия.
 *
 * Например, "24.12.2023".
 */
class DateSingle : DateItem {

    /** Дата занятия. */
    val date: LocalDate

    private val dayOfWeek: DayOfWeek

    /**
     * Конструктор из строки с датой.
     *
     * @param text Строка с датой.
     * @param pattern Формат даты (по умолчанию "yyyy-MM-dd").
     * @throws DateParseException Если формат даты некорректен.
     */
    constructor(text: String, pattern: String = JSON_DATE_PATTERN_V2) {
        try {
            val parseDate: LocalDate = try {
                DateTimeFormat.forPattern(pattern).parseLocalDate(text)
            } catch (_: Exception) {
                DateTimeFormat.forPattern(JSON_DATE_PATTERN).parseLocalDate(text)
            }

            date = parseDate
            dayOfWeek = DayOfWeek.of(date)

        } catch (e: DateDayOfWeekException) {
            throw e

        } catch (e: Exception) {
            throw DateParseException("Invalid parse date: $text", e)
        }
    }

    /**
     * Конструктор из объекта [LocalDate].
     *
     * @param date Дата занятия.
     */
    constructor(date: LocalDate) {
        this.date = date
        dayOfWeek = DayOfWeek.of(this.date)
    }

    /**
     * Возвращает день недели даты.
     */
    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    /**
     * Возвращает периодичность (всегда ONCE).
     */
    override fun frequency(): Frequency = Frequency.ONCE

    /**
     * Проверяет пересечение с другим элементом даты.
     *
     * @param item Элемент даты для проверки.
     * @return true, если даты совпадают или одиночная дата входит в диапазон.
     */
    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
            return this.date == item.date
        }

        if (item is DateRange) {
            return item.intersect(this)
        }

        throw IllegalArgumentException("Invalid intersect object: $item")
    }

    /**
     * Проверяет, находится ли эта дата раньше другой.
     *
     * @param item Другой элемент даты.
     * @return true, если эта дата раньше.
     */
    override fun isBefore(item: DateItem): Boolean {
        if (item is DateSingle) {
            return date.isBefore(item.date)
        }

        if (item is DateRange) {
            return date.isBefore(item.start) && date.isBefore(item.end)
        }

        throw IllegalArgumentException("Invalid compare object: $item")
    }

    /**
     * Создает копию текущего объекта.
     *
     * @return Новый экземпляр [DateSingle].
     */
    override fun clone(): DateItem {
        return DateSingle(date)
    }

    /**
     * Проверяет равенство с другим объектом.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateSingle

        if (date != other.date) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    /**
     * Возвращает хэш-код объекта.
     */
    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + dayOfWeek.hashCode()
        return result
    }

    /**
     * Возвращает строковое представление даты.
     */
    override fun toString(): String {
        return date.toString()
    }

    /**
     * Возвращает строковое представление даты в заданном формате.
     *
     * @param format Формат даты (например, "dd.MM.yyyy").
     * @return Строка с датой.
     */
    fun toString(format: String): String {
        return date.toString(format)
    }
}