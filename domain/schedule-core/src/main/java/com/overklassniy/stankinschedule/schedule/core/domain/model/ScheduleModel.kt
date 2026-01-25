package com.overklassniy.stankinschedule.schedule.core.domain.model


import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Модель расписания.
 */
class ScheduleModel(val info: ScheduleInfo) : Iterable<PairModel> {

    /**
     * Контейнер дней в расписании.
     */
    private val days = linkedMapOf<DayOfWeek, ScheduleDayModel>()

    /**
     * Кэш диапазона дат (start, end). Инвалидируется при изменении расписания.
     */
    @Volatile
    private var dateRangeCache: Pair<LocalDate?, LocalDate?>? = null

    /**
     * Добавляет пару в расписание.
     */
    fun add(pair: PairModel) {
        dayFor(pair).add(pair)
        invalidateCache()
    }

    /**
     * Удаляет пару из расписания.
     */
    fun remove(pair: PairModel) {
        dayFor(pair).remove(pair)
        invalidateCache()
    }

    /**
     * Инвалидирует кэш дат.
     */
    private fun invalidateCache() {
        dateRangeCache = null
    }

    /**
     * Вычисляет диапазон дат за один проход по всем дням.
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
     * Возвращает дату, с которого начинается расписание.
     * Если расписание пустое, то возвращается null.
     */
    fun startDate(): LocalDate? = computeDateRange().first

    /**
     * Возвращает дату, на которую заканчивается расписание.
     * Если расписание пустое, то возвращается null.
     */
    fun endDate(): LocalDate? = computeDateRange().second

    /**
     * Возвращает список всех дисциплин в расписании.
     */
    fun disciplines(): List<String> {
        val disciplines = mutableSetOf<String>()
        for (day in days.values) {
            for (pair in day) {
                disciplines.add(pair.title)
            }
        }

        return disciplines.sorted()
    }

    /**
     * Ограничивает дату, исходя из дат начала и конца расписания.
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
     * Проверяет, является ли расписание пустым.
     */
    fun isEmpty(): Boolean {
        return startDate() == null || endDate() == null
    }

    /**
     * Возвращает список пар, которые есть в заданный день недели.
     */
    fun pairsByDay(dayOfWeek: DayOfWeek): List<PairModel> {
        return dayFor(dayOfWeek).toList()
    }

    /**
     * Возвращает список пар, которые есть в заданный день.
     */
    fun pairsByDate(date: LocalDate): List<PairModel> {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY) {
            return arrayListOf()
        }
        val dayOfWeek = DayOfWeek.of(date)
        return dayFor(dayOfWeek).pairsByDate(date)
    }

    /**
     * Возвращает список всех пар по названию дисциплины.
     */
    fun pairsByDiscipline(discipline: String): List<PairModel> {
        val pairs = arrayListOf<PairModel>()
        for (day in days.values) {
            pairs.addAll(day.pairsByDiscipline(discipline))
        }
        return pairs
    }

    /**
     * Проверяет, можно ли заменить одну пару на другую.
     */
    fun possibleChangePair(old: PairModel?, new: PairModel) {
        dayFor(new).possibleChangePair(old, new)
    }

    /**
     * Заменяет одну пару на другую.
     */
    fun changePair(old: PairModel?, new: PairModel) {
        possibleChangePair(old, new)

        if (old != null) {
            remove(old)
        }
        add(new)
    }

    /**
     * Возвращает день расписания для пары.
     */
    private fun dayFor(pair: PairModel): ScheduleDayModel {
        return dayFor(pair.date.dayOfWeek())
    }

    /**
     * Возвращает день расписания для пары.
     */
    private fun dayFor(dayOfWeek: DayOfWeek): ScheduleDayModel {
        return days.getOrPut(dayOfWeek) { ScheduleDayModel() }
    }

    override fun iterator(): Iterator<PairModel> = days.values.flatten().iterator()
}