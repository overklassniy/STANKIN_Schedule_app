package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Абстрактный класс, представляющий элемент даты в расписании.
 *
 * Может быть либо конкретной датой ([DateSingle]), либо диапазоном дат ([DateRange]).
 * Реализует интерфейс [Comparable] для сравнения элементов по времени.
 */
sealed class DateItem : Comparable<DateItem> {

    companion object {
        /** Разделитель дат в JSON формате. */
        const val JSON_DATE_SEP = "/"

        /** Ключ поля даты в JSON. */
        const val JSON_DATE = "date"

        /** Ключ поля периодичности в JSON. */
        const val JSON_FREQUENCY = "frequency"

        /** Основной формат даты в JSON (yyyy.MM.dd). */
        const val JSON_DATE_PATTERN = "yyyy.MM.dd"

        /** Вторая версия формата даты в JSON (yyyy-MM-dd). */
        const val JSON_DATE_PATTERN_V2 = "yyyy-MM-dd"
    }

    /**
     * Возвращает день недели, к которому относится этот элемент даты.
     *
     * @return [DayOfWeek] - день недели.
     */
    abstract fun dayOfWeek(): DayOfWeek

    /**
     * Возвращает периодичность повторения этого элемента даты.
     *
     * @return [Frequency] - периодичность.
     */
    abstract fun frequency(): Frequency

    /**
     * Проверяет, пересекается ли этот элемент даты с другим.
     *
     * @param item Другой элемент даты для проверки.
     * @return true, если даты пересекаются, иначе false.
     */
    abstract fun intersect(item: DateItem): Boolean

    /**
     * Проверяет, находится ли этот элемент даты раньше другого.
     *
     * @param item Другой элемент даты для сравнения.
     * @return true, если этот элемент раньше, иначе false.
     */
    abstract fun isBefore(item: DateItem): Boolean

    /**
     * Создает копию текущего элемента даты.
     *
     * @return Новый экземпляр [DateItem], идентичный текущему.
     */
    abstract fun clone(): DateItem

    /**
     * Сравнивает текущий элемент с другим.
     *
     * @param other Другой элемент для сравнения.
     * @return -1, если текущий меньше; 1, если больше; 0, если равны.
     */
    override fun compareTo(other: DateItem): Int {
        if (this == other) {
            return 0
        }
        return if (isBefore(other)) -1 else 1
    }

    /**
     * Проверяет равенство с другим объектом.
     *
     * @param other Объект для сравнения.
     * @return true, если объекты равны.
     */
    abstract override fun equals(other: Any?): Boolean

    /**
     * Возвращает хэш-код объекта.
     *
     * @return Хэш-код.
     */
    abstract override fun hashCode(): Int

    /**
     * Возвращает строковое представление элемента даты.
     *
     * @return Строка с датой.
     */
    abstract override fun toString(): String
}