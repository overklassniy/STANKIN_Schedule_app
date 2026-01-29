package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateDayOfWeekException
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateFrequencyException
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateParseException
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Класс, представляющий диапазон дат с определенной периодичностью.
 *
 * Например, "с 01.09 по 31.12 каждую неделю".
 */
class DateRange : DateItem {

    /** Дата начала диапазона. */
    val start: LocalDate

    /** Дата окончания диапазона. */
    val end: LocalDate

    private val frequency: Frequency

    private lateinit var dayOfWeek: DayOfWeek

    /**
     * Конструктор из двух строк с датами и периодичности.
     *
     * @param firstText Строка с датой начала.
     * @param secondText Строка с датой окончания.
     * @param frequencyDate Периодичность ([Frequency]).
     * @param pattern Формат даты (по умолчанию "yyyy-MM-dd").
     * @throws DateParseException Если формат дат некорректен.
     */
    constructor(
        firstText: String,
        secondText: String,
        frequencyDate: Frequency,
        pattern: String = JSON_DATE_PATTERN_V2,
    ) {
        try {
            val (parseStart, parseEnd) = parseDates(firstText, secondText, pattern)
            start = parseStart
            end = parseEnd
            frequency = frequencyDate

        } catch (e: Exception) {
            throw DateParseException(
                "Invalid parse date: $firstText and $secondText",
                e
            )
        }

        init()
    }

    /**
     * Конструктор из строки диапазона (например "date1/date2" или "date1-date2") и периодичности.
     *
     * @param text Строка с диапазоном дат.
     * @param frequencyDate Периодичность.
     * @param pattern Формат даты.
     * @throws DateParseException Если строка не соответствует формату диапазона.
     */
    constructor(text: String, frequencyDate: Frequency, pattern: String = JSON_DATE_PATTERN_V2) {
        var dates = text.split('/')
        if (dates.size != 2) {
            dates = text.split('-')
            if (dates.size != 2) {
                throw DateParseException(
                    "Invalid date text: $text, $dates, frequency: $frequencyDate"
                )
            }
        }

        val (firstText, secondText) = dates

        try {
            val (parseStart, parseEnd) = parseDates(firstText, secondText, pattern)
            start = parseStart
            end = parseEnd
            frequency = frequencyDate

        } catch (e: Exception) {
            throw DateParseException(
                "Invalid parse date: $firstText and $secondText",
                e
            )
        }

        init()
    }

    /**
     * Конструктор из объектов [LocalDate] и периодичности.
     *
     * @param firstDate Дата начала.
     * @param secondDate Дата окончания.
     * @param frequencyDate Периодичность.
     */
    constructor(firstDate: LocalDate, secondDate: LocalDate, frequencyDate: Frequency) {
        start = firstDate
        end = secondDate
        frequency = frequencyDate

        init()
    }

    /**
     * Парсит строки дат в объекты [LocalDate].
     *
     * Пытается распарсить по основному паттерну, в случае неудачи - по запасному.
     */
    private fun parseDates(
        firstText: String,
        secondText: String,
        pattern: String,
    ): Pair<LocalDate, LocalDate> {

        var parseStart: LocalDate
        var parseEnd: LocalDate

        try {
            val formatter = DateTimeFormat.forPattern(pattern)
            parseStart = formatter.parseLocalDate(firstText)
            parseEnd = formatter.parseLocalDate(secondText)

        } catch (_: Exception) {
            val formatter = DateTimeFormat.forPattern(JSON_DATE_PATTERN)
            parseStart = formatter.parseLocalDate(firstText)
            parseEnd = formatter.parseLocalDate(secondText)
        }

        return parseStart to parseEnd
    }

    /**
     * Инициализирует и валидирует поля объекта.
     *
     * Проверяет совпадение дней недели начала и конца, а также корректность периодичности.
     */
    private fun init() {
        if (DayOfWeek.of(start) != DayOfWeek.of(end)) {
            throw DateDayOfWeekException(
                "Invalid day of week: $start - $end"
            )
        }
        dayOfWeek = DayOfWeek.of(start)

        val days = Days.daysBetween(start, end).days
        if (days <= 0 || days % frequency.period != 0) {
            throw DateFrequencyException(
                "Invalid frequency: $start - $end, ${frequency.tag}",
                this.toString(), frequency
            )
        }
    }

    /**
     * Возвращает день недели диапазона.
     */
    override fun dayOfWeek(): DayOfWeek = dayOfWeek

    /**
     * Возвращает периодичность диапазона.
     */
    override fun frequency(): Frequency = frequency

    /**
     * Проверяет пересечение с другим элементом даты.
     *
     * @param item Элемент даты для проверки.
     * @return true, если есть пересечение.
     * @throws IllegalArgumentException Если передан неизвестный тип элемента даты.
     */
    override fun intersect(item: DateItem): Boolean {
        if (item is DateSingle) {
            var it = start
            while (it.isBefore(end) || it == end) {
                if (it == item.date) {
                    return true
                }
                it = it.plusDays(frequency.period)
            }
            return false
        }

        if (item is DateRange) {
            var firstIt = start
            var secondIt = item.start

            while (firstIt.isBefore(end) || firstIt == end) {
                while (secondIt.isBefore(item.end) || secondIt == item.end) {
                    if (firstIt == secondIt) {
                        return true
                    }
                    secondIt = secondIt.plusDays(item.frequency.period)
                }
                firstIt = firstIt.plusDays(frequency.period)
            }

            return false
        }

        throw IllegalArgumentException("Invalid intersect object: $item")
    }

    /**
     * Проверяет, находится ли этот диапазон раньше другого элемента.
     *
     * @param item Другой элемент даты.
     * @return true, если этот диапазон раньше.
     * @throws IllegalArgumentException Если передан неизвестный тип элемента даты.
     */
    override fun isBefore(item: DateItem): Boolean {
        if (item is DateSingle) {
            return start.isBefore(item.date) && end.isBefore(item.date)
        }

        if (item is DateRange) {
            return start.isBefore(item.start) && end.isBefore(item.end)
        }

        throw IllegalArgumentException("Invalid compare object: $item")
    }

    /**
     * Создает копию текущего диапазона.
     *
     * @return Новый экземпляр [DateRange].
     */
    override fun clone(): DateItem {
        return DateRange(start, end, frequency)
    }

    /**
     * Проверяет равенство с другим объектом.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateRange

        if (start != other.start) return false
        if (end != other.end) return false
        if (frequency != other.frequency) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    /**
     * Возвращает хэш-код объекта.
     */
    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + frequency.hashCode()
        result = 31 * result + dayOfWeek.hashCode()
        return result
    }

    /**
     * Возвращает строковое представление диапазона.
     */
    override fun toString(): String {
        return "$start-$end"
    }

    /**
     * Возвращает строковое представление диапазона с заданным форматом и разделителем.
     *
     * @param format Формат даты (например, "dd.MM.yyyy").
     * @param delimiter Разделитель между датами.
     * @return Строка диапазона.
     */
    fun toString(format: String, delimiter: String): String {
        return start.toString(format) + delimiter + end.toString(format)
    }
}