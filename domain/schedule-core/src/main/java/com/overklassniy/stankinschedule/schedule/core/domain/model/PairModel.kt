package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Модель учебной пары (занятия).
 *
 * @property title Название дисциплины.
 * @property lecturer Преподаватель.
 * @property classroom Аудитория.
 * @property type Тип занятия (лекция, семинар и т.д.).
 * @property subgroup Подгруппа.
 * @property time Время проведения.
 * @property date Даты проведения.
 * @property info Служебная информация (ID).
 */
data class PairModel(
    val title: String,
    val lecturer: String,
    val classroom: String,
    val type: Type,
    val subgroup: Subgroup,
    val time: Time,
    val date: DateModel,
    val info: PairInfo = PairInfo()
) : Comparable<PairModel> {

    /**
     * Проверяет, пересекается ли эта пара с другой по времени, дате и подгруппе.
     *
     * @param other Другая пара.
     * @return true, если пары пересекаются.
     */
    fun isIntersect(other: PairModel): Boolean {
        return time.isIntersect(other.time) &&
                date.intersect(other.date) &&
                subgroup.isIntersect(other.subgroup)
    }

    /**
     * Проверяет, относится ли пара к указанной подгруппе.
     *
     * @param subgroup Подгруппа для проверки.
     * @return true, если пара для всех (COMMON) или совпадает с указанной подгруппой.
     */
    fun isCurrently(subgroup: Subgroup): Boolean {
        return this.subgroup == Subgroup.COMMON ||
                subgroup == Subgroup.COMMON ||
                this.subgroup == subgroup
    }

    /**
     * Сравнивает пары для сортировки.
     *
     * Сначала по времени начала, затем по подгруппе.
     *
     * @param other Другая пара.
     * @return Результат сравнения (-1, 0, 1).
     */
    override fun compareTo(other: PairModel): Int {
        if (time.start == other.time.start) {
            return subgroup.compareTo(other.subgroup)
        }
        return time.start.compareTo(other.time.start)
    }

    /**
     * Возвращает строковое представление пары.
     */
    override fun toString(): String {
        return "$title. $lecturer. $classroom. $type. $subgroup. $time. $date"
    }
}