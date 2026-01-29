package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.core.domain.ext.removeIf7
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.PairIntersectException
import org.joda.time.LocalDate

/**
 * Модель расписания на один день недели.
 *
 * Содержит список пар ([PairModel]), проходящих в этот день недели.
 */
class ScheduleDayModel : Iterable<PairModel> {
    /** Список пар в этот день. */
    private val pairs = arrayListOf<PairModel>()

    /** Кэш диапазона дат (начало и конец). */
    @Volatile
    private var dateRangeCache: Pair<LocalDate?, LocalDate?>? = null

    /**
     * Добавляет пару в расписание дня.
     *
     * @param pair Пара для добавления.
     * @throws PairIntersectException Если пара пересекается с уже существующими.
     */
    fun add(pair: PairModel) {
        isAddCheck(pair)
        pairs.add(pair)
        invalidateCache()
    }

    /**
     * Удаляет пару из расписания дня.
     *
     * @param pair Пара для удаления.
     */
    fun remove(pair: PairModel) {
        pairs.removeIf7 { it == pair }
        invalidateCache()
    }

    /**
     * Сбрасывает кэш диапазона дат.
     */
    private fun invalidateCache() {
        dateRangeCache = null
    }

    /**
     * Вычисляет диапазон дат для всех пар в этот день.
     *
     * @return Пара (начало, конец) или (null, null) если пар нет.
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
     * Возвращает дату начала самой ранней пары в этом дне.
     *
     * @return [LocalDate] или null, если пар нет.
     */
    fun startDate(): LocalDate? = computeDateRange().first

    /**
     * Возвращает дату окончания самой поздней пары в этом дне.
     *
     * @return [LocalDate] или null, если пар нет.
     */
    fun endDate(): LocalDate? = computeDateRange().second

    /**
     * Проверяет, можно ли добавить пару (нет ли пересечений).
     *
     * @param added Добавляемая пара.
     * @throws PairIntersectException Если есть пересечение.
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
     * Проверяет возможность замены пары на новую без конфликтов.
     *
     * @param old Старая пара (для игнорирования при проверке пересечений), может быть null.
     * @param new Новая пара.
     * @throws PairIntersectException Если новая пара пересекается с другими.
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
     * Возвращает список пар на конкретную дату.
     *
     * @param date Дата для фильтрации.
     * @return Список пар, проходящих в указанную дату.
     */
    fun pairsByDate(date: LocalDate): List<PairModel> {
        return pairs.asSequence()
            .filter { it.date.intersect(date) }
            .sortedWith(compareBy { it })
            .toList()
    }

    /**
     * Возвращает итератор по всем парам этого дня недели.
     */
    override fun iterator(): Iterator<PairModel> = pairs.iterator()
}