package com.overklassniy.stankinschedule.schedule.table.domain.model

import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/** Базовая ширина страницы, относительно которой вычисляется масштаб таблицы. */
private const val BASE_PAGE_WIDTH = 1920f

/** Доля высоты страницы, используемая как внутренний отступ. */
private const val PAGE_PADDING_RATIO = 0.05f

/**
 * Модель для отрисовки таблицы расписания на Canvas.
 *
 * Содержит предварительно вычисленные размеры, шрифты и макеты ячеек.
 *
 * @property scheduleName Название расписания.
 * @property lines Количество строк на каждый день недели.
 * @property pageHeight Высота страницы.
 * @property pageWidth Ширина страницы.
 * @property tableColor Цвет таблицы (линии и текст).
 * @property pagePadding Внутренний отступ от границ страницы.
 */
class DrawScheduleTable(
    val scheduleName: String,
    val lines: List<Int>,
    tableDays: Map<DayOfWeek, ScheduleTableDay>,
    val pageHeight: Float,
    val pageWidth: Float,
    val tableColor: Int = Color.BLACK,
    scale: Float = pageWidth / BASE_PAGE_WIDTH,
    val pagePadding: Float = pageHeight * PAGE_PADDING_RATIO
) {
    val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = tableColor
    }
    val linePaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = tableColor
        strokeWidth = 1f
    }

    val titleSize = 28f * scale
    val headerSize = 32f * scale
    val textSize = 14f * scale
    val textPadding = 4 * scale

    private val days: MutableMap<DayOfWeek, List<DrawScheduleTableCell>> = mutableMapOf()

    val drawWidth = pageWidth - 2 * pagePadding
    val drawHeight = pageHeight - 2 * pagePadding

    val titleHeight = textPaint.withSize(titleSize) { it.lineHeight() }
    val titleBottomPadding = pagePadding / 8

    val columnWidth = (drawWidth - headerSize) / COLUMN_COUNT
    val rowHeight = (drawHeight - headerSize - titleHeight - titleBottomPadding) / ROW_COUNT

    init {
        for ((index, lineCount) in lines.withIndex()) {
            val subRowHeight = rowHeight / lineCount

            val day = DayOfWeek.entries[index]
            val tableDay = tableDays.getOrElse(day) { ScheduleTableDay() }

            days[day] = tableDay
                .cells { pairs ->
                    pairs.joinToString(separator = "\n", transform = ::pairForTable)
                }.map { cell ->
                    DrawScheduleTableCell(
                        cell = cell,
                        width = (columnWidth * cell.columnSpan - 2 * textPadding).toInt(),
                        height = (subRowHeight * cell.rowSpan - 2 * textPadding).toInt(),
                        fontSize = textSize,
                        textPaint = textPaint
                    )
                }
        }
    }

    /**
     * Возвращает список подготовленных ячеек для указанного дня недели.
     *
     * @param day День недели.
     * @return Список ячеек для отрисовки.
     */
    operator fun get(day: DayOfWeek): List<DrawScheduleTableCell> {
        return days.getOrElse(day) { emptyList() }
    }

    companion object {
        const val ROW_COUNT = 6
        const val COLUMN_COUNT = 8

        /**
         * Формирует человекочитаемую строку для отображения пары в ячейке таблицы.
         *
         * @param pair Модель пары.
         * @return Строка для отображения в ячейке.
         */
        fun pairForTable(pair: PairModel): String {
            // Title. Lecture. Type. Subgroup. Classroom. [Date1, Date2...]
            return buildString {
                append(pair.title)
                append(". ")
                if (pair.lecturer.isNotEmpty()) append(pair.lecturer).append(". ")
                append(typeLabel(pair.type))
                append(". ")
                if (pair.subgroup.isShow()) append(subgroupLabel(pair.subgroup)).append(". ")
                if (pair.classroom.isNotEmpty()) append(pair.classroom).append(". ")
                append("[")
                append(pair.date.joinToString(", ", transform = ::dateLabel))
                append("]")
            }
        }

        /**
         * Возвращает русское название типа занятия.
         */
        private fun typeLabel(type: Type): String {
            return when (type) {
                Type.LECTURE -> "Лекция"
                Type.SEMINAR -> "Семинар"
                Type.LABORATORY -> "Лабораторные занятия"
            }
        }

        /**
         * Возвращает обозначение подгруппы для ячейки.
         */
        private fun subgroupLabel(subgroup: Subgroup): String {
            return when (subgroup) {
                Subgroup.A -> "(А)"
                Subgroup.B -> "(Б)"
                Subgroup.COMMON -> ""
            }
        }

        /**
         * Форматирует элемент даты пары (одиночная дата или диапазон с периодичностью).
         */
        private fun dateLabel(date: DateItem): String {
            return when (date) {
                is DateSingle -> date.toString("dd.MM")
                is DateRange -> date.toString("dd.MM", "-") + " " + when (date.frequency()) {
                    Frequency.ONCE -> ""
                    Frequency.EVERY -> "к.н."
                    Frequency.THROUGHOUT -> "ч.н."
                }

                else -> ""
            }
        }
    }
}

/**
 * Преобразует логическую модель таблицы расписания [ScheduleTable] в модель для отрисовки [DrawScheduleTable].
 *
 * @param pageHeight Высота страницы.
 * @param pageWidth Ширина страницы.
 * @param tableColor Цвет таблицы.
 * @return Модель для отрисовки.
 */
fun ScheduleTable.toDraw(
    pageHeight: Float,
    pageWidth: Float,
    tableColor: Int = Color.BLACK,
): DrawScheduleTable {
    return DrawScheduleTable(
        scheduleName = scheduleName,
        lines = linesPerDay(),
        tableDays = days,
        pageHeight = pageHeight,
        pageWidth = pageWidth,
        tableColor = tableColor
    )
}
