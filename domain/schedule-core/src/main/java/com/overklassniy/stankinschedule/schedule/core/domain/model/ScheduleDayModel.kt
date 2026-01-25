package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.core.domain.ext.removeIf7
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.PairIntersectException
import org.joda.time.LocalDate

/**
 * День в расписании.
 */
class ScheduleDayModel : Iterable<PairModel> {

    /**
     * Пары в дне.
     */
    private val pairs = arrayListOf<PairModel>()

    /**
     * Кэш диапазона дат (start, end). Инвалидируется при изменении пар.
     */
    @Volatile
    private var dateRangeCache: Pair<LocalDate?, LocalDate?>? = null

    /**
     * Добавляет пару в день.
     */
    fun add(pair: PairModel) {
        isAddCheck(pair)
        pairs.add(pair)
        invalidateCache()
    }

    /**
     * Удаляет пару.
     */
    fun remove(pair: PairModel) {
        pairs.removeIf7 { it == pair }
        invalidateCache()
    }

    /**
     * Инвалидирует кэш дат.
     */
    private fun invalidateCache() {
        dateRangeCache = null
    }

    /**
     * Вычисляет диапазон дат за один проход.
     */
    private fun computeDateRange(): Pair<LocalDate?, LocalDate?> {
        dateRangeCache?.let { return it }

        if (pairs.isEmpty()) {
            return (null to null).also { dateRangeCache = it }
        }

        var first: LocalDate? = null
        var last: LocalDate? = null

        for (pair in pairs) {
            val pairStart = pair.date.startDate()
            val pairEnd = pair.date.endDate()

            if (pairStart != null) {
                first = if (first == null || pairStart < first) pairStart else first
            }
            if (pairEnd != null) {
                last = if (last == null || pairEnd > last) pairEnd else last
            }
        }

        return (first to last).also { dateRangeCache = it }
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
     * Проверяет, можно ли добавить пару в расписание.
     * Использует раннее прерывание при нахождении конфликта.
     */
    @Throws(PairIntersectException::class)
    private fun isAddCheck(added: PairModel) {
        for (pair in pairs) {
            if (added.isIntersect(pair)) {
                throw PairIntersectException(
                    "There can't be two pairs at the same time: '$pair' and '$added'",
                    pair,
                    added
                )
            }
        }
    }

    /**
     * Проверяет, можно ли заменить пару в расписании.
     */
    @Throws(PairIntersectException::class)
    fun possibleChangePair(old: PairModel?, new: PairModel) {
        for (pair in pairs) {
            if (pair != old && new.isIntersect(pair)) {
                throw PairIntersectException(
                    "There can't be two pairs at the same time: '$pair' and '$new'",
                    pair,
                    new
                )
            }
        }
    }

    /**
     * Возвращает список пар, которые есть в заданный день.
     * Оптимизировано: фильтрация с использованием asSequence для ленивой обработки.
     */
    fun pairsByDate(date: LocalDate): List<PairModel> {
        return pairs.asSequence()
            .filter { it.date.intersect(date) }
            .sortedWith(compareBy { it })
            .toList()
    }

    /**
     * Возвращает список всех пар по названию дисциплины.
     */
    fun pairsByDiscipline(discipline: String): List<PairModel> {
        return pairs.filter { it.title == discipline }
    }

    override fun iterator(): Iterator<PairModel> = pairs.iterator()
}