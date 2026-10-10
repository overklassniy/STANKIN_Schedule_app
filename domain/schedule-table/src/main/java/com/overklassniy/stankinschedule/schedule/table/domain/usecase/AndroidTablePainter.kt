package com.overklassniy.stankinschedule.schedule.table.domain.usecase

import android.graphics.Canvas
import androidx.core.graphics.withTranslation
import com.overklassniy.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.table.domain.model.DrawScheduleTable
import com.overklassniy.stankinschedule.schedule.table.domain.model.drawCenterText
import com.overklassniy.stankinschedule.schedule.table.domain.model.drawText
import com.overklassniy.stankinschedule.schedule.table.domain.model.fontSizeForHeight

/**
 * Отрисовывает таблицу расписания на [Canvas].
 *
 * Рисует заголовок, заголовки строк/столбцов и содержимое ячеек на основе
 * подготовленной модели [DrawScheduleTable]. Подбирает размер шрифта для
 * заголовков и центрирует текст в ячейках.
 *
 * @param drawScheduleTable Модель с предвычисленными размерами, шрифтами и макетами ячеек.
 */
fun Canvas.drawScheduleTable(
    drawScheduleTable: DrawScheduleTable,
) {
    val x = drawScheduleTable.pagePadding
    var y = drawScheduleTable.pagePadding

    val titleHeight = drawText(
        text = drawScheduleTable.scheduleName,
        x = x + drawScheduleTable.drawWidth / 2,
        y = y,
        fontSize = drawScheduleTable.titleSize,
        paint = drawScheduleTable.textPaint
    )

    y += titleHeight + drawScheduleTable.titleBottomPadding

    drawRect(
        x,
        y,
        x + drawScheduleTable.headerSize,
        y + drawScheduleTable.headerSize,
        drawScheduleTable.linePaint
    )

    val headerFontSize = fontSizeForHeight(
        drawScheduleTable.headerSize - 2 * drawScheduleTable.textPadding,
        drawScheduleTable.textPaint
    )

    drawDayHeaders(drawScheduleTable, x, y, headerFontSize)
    drawTimeHeaders(drawScheduleTable, x, y, headerFontSize)
    drawTableCells(drawScheduleTable, x, y)
}

/**
 * Рисует заголовки строк с названиями дней недели.
 */
private fun Canvas.drawDayHeaders(
    table: DrawScheduleTable,
    x: Float,
    y: Float,
    headerFontSize: Float,
) {
    val rowHeight = table.rowHeight
    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    for (i in 0 until DrawScheduleTable.ROW_COUNT) {
        val cellY = y + table.headerSize + i * rowHeight

        drawRect(x, cellY, x + table.headerSize, cellY + rowHeight, table.linePaint)
        drawCenterText(
            text = daysOfWeek[i],
            x = x,
            y = cellY,
            w = table.headerSize,
            h = rowHeight,
            fontSize = headerFontSize,
            rotate = -90,
            paint = table.textPaint
        )
    }
}

/**
 * Рисует заголовки колонок с интервалами времени пар.
 */
private fun Canvas.drawTimeHeaders(
    table: DrawScheduleTable,
    x: Float,
    y: Float,
    headerFontSize: Float,
) {
    val columnWidth = table.columnWidth
    val times = Time.STARTS.zip(Time.ENDS) { a, b -> "$a - $b" }

    for (i in 0 until DrawScheduleTable.COLUMN_COUNT) {
        val cellX = x + table.headerSize + i * columnWidth

        drawRect(cellX, y, cellX + columnWidth, y + table.headerSize, table.linePaint)
        drawCenterText(
            text = times[i],
            x = cellX,
            y = y,
            w = columnWidth,
            h = table.headerSize,
            fontSize = headerFontSize,
            paint = table.textPaint
        )
    }
}

/**
 * Рисует содержимое ячеек расписания с учётом объединения строк и колонок.
 */
private fun Canvas.drawTableCells(
    table: DrawScheduleTable,
    x: Float,
    y: Float,
) {
    val columnWidth = table.columnWidth
    val rowHeight = table.rowHeight

    for ((index, rowCount) in table.lines.withIndex()) {
        val cells = table[DayOfWeek.entries[index]]
        val subRowHeight = rowHeight / rowCount

        for (cell in cells) {
            val cellX = x + table.headerSize + cell.column * columnWidth
            val cellY = y + table.headerSize + index * rowHeight + cell.row * subRowHeight
            val cellW = cellX + columnWidth * cell.columnSpan
            val cellH = cellY + subRowHeight * cell.rowSpan

            drawRect(cellX, cellY, cellW, cellH, table.linePaint)
            cell.layout?.let { layout ->
                withTranslation(
                    x = cellX + table.textPadding,
                    y = cellY + table.textPadding,
                ) {
                    layout.draw(this)
                }
            }
        }
    }
}
