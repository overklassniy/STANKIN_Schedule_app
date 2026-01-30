package com.overklassniy.stankinschedule.schedule.core.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Форматтер для человекочитаемого представления пары.
 *
 * Собирает строку вида: «Название. Преподаватель. Тип. Подгруппа. &#91;Даты&#93;».
 */
class PairFormatter {

    /**
     * Формирует строковое описание пары для отображения.
     *
     * Алгоритм:
     * 1. Добавляет название, преподавателя и тип пары.
     * 2. При наличии подгруппы добавляет её обозначение.
     * 3. Добавляет блок дат в квадратных скобках.
     *
     * @param pair Модель пары.
     * @return Строка описания пары.
     */
    fun format(pair: PairModel): String {
        return buildString {
            append(pair.title)
            append(". ")

            append(pair.lecturer)
            append(". ")

            append(pairType(pair.type))
            append(". ")

            val subgroupText = pairSubgroup(pair.subgroup)
            if (subgroupText != null) {
                append(subgroupText)
                append(". ")
            }

            append(pairDate(pair.date))
        }
    }

    /**
     * Возвращает текстовое название типа пары.
     *
     * @param type Тип пары.
     * @return Локализованная строка типа пары.
     */
    private fun pairType(type: Type): String {
        return when (type) {
            Type.LECTURE -> "Лекция"
            Type.SEMINAR -> "Семинар"
            Type.LABORATORY -> "Лабораторные занятия"
        }
    }

    /**
     * Возвращает обозначение подгруппы.
     *
     * @param subgroup Подгруппа.
     * @return "(А)" или "(Б)", либо null для общей группы.
     */
    private fun pairSubgroup(subgroup: Subgroup): String? {
        return when (subgroup) {
            Subgroup.COMMON -> return null
            Subgroup.A -> "(А)"
            Subgroup.B -> "(Б)"
        }
    }

    /**
     * Формирует блок дат в квадратных скобках.
     *
     * Формат: [dd.MM, dd.MM, dd.MM-dd.MM аббревиатура_частоты]
     *
     * @param date Модель набора дат (синглы и диапазоны).
     * @return Строка с датами.
     */
    private fun pairDate(date: DateModel): String {
        // Формируем список дат через joinToString с разделителем ", "
        // Для диапазонов добавляем частоту (к.н. – каждая неделя, ч.н. – через неделю)
        return "[" +
                date.joinToString(
                    separator = ", ",
                    transform = { item ->
                        when (item) {
                            is DateSingle -> {
                                item.toString("dd.MM")
                            }

                            is DateRange -> {
                                item.toString("dd.MM", "-") + " " +
                                        pairDateFrequency(item.frequency())
                            }
                        }
                    }
                ) +
                "]"
    }

    /**
     * Возвращает аббревиатуру частоты повторения.
     *
     * Обозначения:
     * - "" — единичное событие
     * - "к.н." — каждая неделя (еженедельно)
     * - "ч.н." — через неделю (по нечётным/чётным)
     *
     * @param frequency Частота повторения.
     * @return Аббревиатура.
     */
    private fun pairDateFrequency(frequency: Frequency): String {
        return when (frequency) {
            Frequency.ONCE -> return ""
            Frequency.EVERY -> "к.н."
            Frequency.THROUGHOUT -> "ч.н."
        }
    }
}





