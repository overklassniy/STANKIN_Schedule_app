package com.overklassniy.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName
import com.overklassniy.stankinschedule.journal.core.domain.model.Discipline.Companion.NO_FACTOR
import com.overklassniy.stankinschedule.journal.core.domain.model.Discipline.Companion.NO_MARK

/**
 * Модель учебной дисциплины.
 *
 * Содержит информацию о названии предмета, оценках по различным типам контроля и коэффициенте.
 *
 * @property title Название дисциплины.
 * @property marks Словарь оценок, где ключ - тип оценки [MarkType], значение - баллы.
 * @property factor Коэффициент значимости дисциплины (используется для расчета рейтинга).
 */
data class Discipline(
    @SerializedName("title") val title: String = "",
    @SerializedName("marks") private val marks: LinkedHashMap<MarkType, Int> = linkedMapOf(),
    @SerializedName("factor") val factor: Double = NO_FACTOR,
) : Iterable<Pair<MarkType, Int>> {

    /**
     * Проверяет, завершена ли дисциплина (выставлены ли все оценки).
     *
     * Дисциплина считается завершенной, если для всех типов контроля выставлены оценки, отличные от [NO_MARK],
     * и коэффициент не равен [NO_FACTOR].
     *
     * @return `true`, если дисциплина завершена, иначе `false`.
     */
    fun isCompleted(): Boolean {
        for (mark in marks) {
            if (mark.value == NO_MARK) {
                return false
            }
        }
        return factor != NO_FACTOR
    }

    /**
     * Получает оценку по указанному типу контроля.
     *
     * @param type Тип контроля [MarkType].
     * @return Оценка (баллы) или `null`, если оценка не найдена.
     */
    operator fun get(type: MarkType): Int? {
        return marks[type]
    }

    /**
     * Устанавливает оценку для указанного типа контроля.
     *
     * @param type Тип контроля [MarkType].
     * @param value Значение оценки (баллы).
     */
    operator fun set(type: MarkType, value: Int) {
        marks[type] = value
    }

    /**
     * Возвращает итератор по оценкам дисциплины.
     *
     * @return Итератор пар (Тип оценки, Значение).
     */
    override fun iterator(): Iterator<Pair<MarkType, Int>> = marks
        .map { (key, value) -> (key to value) }
        .iterator()

    override fun toString(): String {
        return "Discipline(title='$title', marks=$marks, factor=$factor)"
    }

    companion object {

        /** Значение, обозначающее отсутствие оценки. */
        const val NO_MARK = 0

        /** Значение, обозначающее отсутствие коэффициента. */
        const val NO_FACTOR = 0.0
    }
}