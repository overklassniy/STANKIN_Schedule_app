package com.overklassniy.stankinschedule.schedule.core.domain.model


import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Основная модель расписания.
 *
 * Содержит информацию о расписании ([ScheduleInfo]) и карту дней недели с парами ([ScheduleDayModel]).
 * Предоставляет методы для управления парами (добавление, удаление, изменение) и получения информации о датах.
 */
class ScheduleModel(val info: ScheduleInfo) : Iterable<PairModel> {

    private val days = linkedMapOf<DayOfWeek, ScheduleDayModel>()

    @Volatile
    private var dateRangeCache: Pair<LocalDate?, LocalDate?>? = null

    /**
     * Добавляет пару в расписание.
     *
     * @param pair Пара для добавления.
     * @throws `PairIntersectException` Если пара пересекается с существующими.
     */
    fun add(pair: PairModel) {
        dayFor(pair).add(pair)
        invalidateCache()
    }

    /**
     * Удаляет пару из расписания.
     *
     * @param pair Пара для удаления.
     */
    fun remove(pair: PairModel) {
        dayFor(pair).remove(pair)
        invalidateCache()
    }

    /**
     * Сбрасывает кэш диапазона дат.
     */
    private fun invalidateCache() {
        dateRangeCache = null
    }

    /**
     * Вычисляет диапазон дат семестра (по всем дням).
     *
     * @return Пара (начало, конец).
     */
    private fun computeDateRange(): Pair<LocalDate?, LocalDate?> {
        dateRangeCache?.let { return it }

        var start: LocalDate? = null
        var last: LocalDate? = null

        for (day in days.values) {
            val dayStart = day.startDate()
            val dayEnd = day.endDate()

            if (dayStart != null) {
                start = if (start == null || dayStart < start) dayStart else start
            }
            if (dayEnd != null) {
                last = if (last == null || dayEnd > last) dayEnd else last
            }
        }

        return (start to last).also { dateRangeCache = it }
    }

    /**
     * Возвращает дату начала семестра (самая ранняя дата пары).
     *
     * @return [LocalDate] или null, если расписание пустое.
     */
    fun startDate(): LocalDate? = computeDateRange().first

    /**
     * Возвращает дату окончания семестра (самая поздняя дата пары).
     *
     * @return [LocalDate] или null, если расписание пустое.
     */
    fun endDate(): LocalDate? = computeDateRange().second

    /**
     * Ограничивает дату в пределах семестра.
     *
     * Если переданная дата раньше начала семестра, возвращается дата начала.
     * Если позже окончания - дата окончания.
     * Иначе возвращается сама дата.
     *
     * @param date Дата для проверки.
     * @return Ограниченная дата.
     */
    fun limitDate(date: LocalDate): LocalDate {
        startDate()?.let {
            if (date.isBefore(it)) {
                return it
            }
        }

        endDate()?.let {
            if (date.isAfter(it)) {
                return it
            }
        }

        return date
    }

    /**
     * Проверяет, пустое ли расписание.
     *
     * @return true, если нет дат начала или окончания.
     */
    fun isEmpty(): Boolean {
        return startDate() == null || endDate() == null
    }

    /**
     * Получает список пар на указанный день недели.
     *
     * @param dayOfWeek День недели.
     * @return Список пар.
     */
    fun pairsByDay(dayOfWeek: DayOfWeek): List<PairModel> {
        return dayFor(dayOfWeek).toList()
    }

    /**
     * Получает список пар на конкретную дату.
     *
     * @param date Дата.
     * @return Список пар.
     */
    fun pairsByDate(date: LocalDate): List<PairModel> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return dayFor(dayOfWeek).pairsByDate(date)
    }

    /**
     * Проверяет возможность изменения пары (отсутствие конфликтов).
     *
     * @param old Старая пара (если есть).
     * @param new Новая пара.
     * @throws `PairIntersectException` Если новая пара создает конфликт.
     */
    fun possibleChangePair(old: PairModel?, new: PairModel) {
        dayFor(new).possibleChangePair(old, new)
    }

    /**
     * Изменяет пару в расписании (удаляет старую и добавляет новую).
     *
     * @param old Старая пара (если null, то только добавление).
     * @param new Новая пара.
     */
    fun changePair(old: PairModel?, new: PairModel) {
        possibleChangePair(old, new)

        if (old != null) {
            remove(old)
        }
        add(new)
    }

    /**
     * Возвращает модель дня для указанной пары.
     */
    private fun dayFor(pair: PairModel): ScheduleDayModel {
        return dayFor(pair.date.dayOfWeek())
    }

    /**
     * Возвращает модель дня по дню недели (создает новую, если нет).
     */
    private fun dayFor(dayOfWeek: DayOfWeek): ScheduleDayModel {
        return days.getOrPut(dayOfWeek) { ScheduleDayModel() }
    }

    /**
     * Возвращает итератор по всем парам расписания (во всех днях).
     *
     * @return Итератор [PairModel].
     */
    override fun iterator(): Iterator<PairModel> = days.values.flatten().iterator()
}