package com.overklassniy.stankinschedule.schedule.core.domain.model

import com.overklassniy.stankinschedule.core.domain.ext.removeIf7
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateDayOfWeekException
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateIntersectException
import org.joda.time.LocalDate
import java.util.TreeSet

/**
 * Модель, представляющая набор дат проведения занятий.
 *
 * Содержит коллекцию [DateItem], которые могут быть как одиночными датами, так и диапазонами.
 * Гарантирует, что все даты относятся к одному дню недели.
 */
class DateModel : Cloneable, Iterable<DateItem> {

    private val dates: TreeSet<DateItem> = sortedSetOf()
    private var dayOfWeek: DayOfWeek? = null

    /**
     * Добавляет элемент даты в модель.
     *
     * @param item Элемент даты ([DateItem]) для добавления.
     * @throws DateDayOfWeekException Если день недели добавляемой даты не совпадает с существующими.
     * @throws DateIntersectException Если добавляемая дата пересекается с уже существующими.
     */
    fun add(item: DateItem) {
        possibleChange(null, item)

        dates.add(item)
        dayOfWeek = item.dayOfWeek()
    }

    /**
     * Удаляет элемент даты из модели.
     *
     * @param item Элемент даты для удаления. Если null, ничего не происходит.
     */
    fun remove(item: DateItem?) {
        if (item != null) {
            dates.remove(item)
            if (dates.isEmpty()) {
                dayOfWeek = null
            }
        }
    }

    /**
     * Удаляет элемент даты по индексу.
     *
     * @param position Индекс элемента для удаления.
     * @return Удаленный элемент [DateItem].
     */
    fun remove(position: Int): DateItem {
        val item = dates.elementAt(position)
        dates.removeIf7 { item == it }
        if (dates.isEmpty()) {
            dayOfWeek = null
        }
        return item
    }

    /**
     * Возвращает элемент даты по индексу.
     *
     * @param position Индекс элемента.
     * @return Элемент [DateItem].
     */
    fun get(position: Int): DateItem = dates.elementAt(position)

    /**
     * Возвращает самую раннюю дату начала занятий в этом наборе.
     *
     * @return [LocalDate] начала или null, если набор пуст.
     */
    fun startDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }

        return when (val startDate = dates.first()) {
            is DateSingle -> startDate.date
            is DateRange -> startDate.start
        }
    }

    /**
     * Возвращает самую позднюю дату окончания занятий в этом наборе.
     *
     * @return [LocalDate] окончания или null, если набор пуст.
     */
    fun endDate(): LocalDate? {
        if (dates.isEmpty()) {
            return null
        }

        return when (val startDate = dates.last()) {
            is DateSingle -> startDate.date
            is DateRange -> startDate.end
        }
    }

    /**
     * Проверяет пересечение с элементом даты.
     *
     * @param item Элемент даты для проверки.
     * @return true, если есть пересечение, иначе false.
     */
    fun intersect(item: DateItem): Boolean {
        for (date in dates) {
            if (date.intersect(item)) {
                return true
            }
        }
        return false
    }

    /**
     * Проверяет пересечение с другой моделью дат.
     *
     * @param other Другая модель дат ([DateModel]).
     * @return true, если есть пересечение хотя бы одной даты, иначе false.
     */
    fun intersect(other: DateModel): Boolean {
        for (date in other.dates) {
            if (intersect(date)) {
                return true
            }
        }
        return false
    }

    /**
     * Проверяет, попадает ли конкретная дата в этот набор.
     *
     * @param item Дата [LocalDate] для проверки.
     * @return true, если дата входит в набор, иначе false.
     */
    fun intersect(item: LocalDate): Boolean {
        val dateItem = DateSingle(item)
        return intersect(dateItem)
    }

    /**
     * Возвращает день недели для этого набора дат.
     *
     * @return [DayOfWeek] день недели.
     * @throws NullPointerException если набор пуст.
     */
    fun dayOfWeek() = dayOfWeek!!

    /**
     * Возвращает итератор по элементам дат.
     *
     * @return Итератор [Iterator]<[DateItem]>.
     */
    override fun iterator(): Iterator<DateItem> = dates.iterator()

    /**
     * Проверяет, пуст ли набор дат.
     *
     * @return true, если набор пуст, иначе false.
     */
    fun isEmpty(): Boolean = dates.isEmpty()

    /**
     * Создает глубокую копию модели.
     *
     * @return Новый экземпляр [DateModel] с копиями всех элементов.
     */
    public override fun clone(): DateModel {
        val date = DateModel()
        for (d in dates) {
            date.add(d.clone())
        }
        return date
    }

    /**
     * Проверяет возможность замены или добавления даты.
     *
     * Проверяет совместимость дня недели и отсутствие пересечений с другими датами.
     *
     * @param oldDate Старая дата, которую планируется заменить (может быть null при добавлении).
     * @param newDate Новая дата.
     * @throws DateDayOfWeekException Если день недели новой даты отличается от текущего дня недели модели.
     * @throws DateIntersectException Если новая дата пересекается с существующими (кроме oldDate).
     */
    @Throws(DateDayOfWeekException::class, DateIntersectException::class)
    fun possibleChange(oldDate: DateItem?, newDate: DateItem) {
        if (!(oldDate != null && dates.size == 1)) {
            if (dayOfWeek != null && dayOfWeek != newDate.dayOfWeek()) {
                throw DateDayOfWeekException(
                    "Invalid day of week: ${newDate.dayOfWeek()} and $dayOfWeek"
                )
            }
        }

        for (date in dates) {
            if (date != oldDate) {
                if (date.intersect(newDate)) {
                    throw DateIntersectException(
                        "Date is intersect: $date and $newDate"
                    )
                }
            }
        }
    }

    /**
     * Проверяет равенство с другим объектом.
     *
     * @param other Объект для сравнения.
     * @return true, если объекты равны.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateModel

        if (dates != other.dates) return false
        if (dayOfWeek != other.dayOfWeek) return false

        return true
    }

    /**
     * Возвращает хэш-код объекта.
     *
     * @return Хэш-код.
     */
    override fun hashCode(): Int {
        var result = dates.hashCode()
        result = 31 * result + (dayOfWeek?.hashCode() ?: 0)
        return result
    }

    /**
     * Возвращает строковое представление модели дат.
     *
     * @return Строка со списком дат.
     */
    override fun toString(): String {
        return "[" + dates.joinToString(", ") + "]"
    }
}