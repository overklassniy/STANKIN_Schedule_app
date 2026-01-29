package com.overklassniy.stankinschedule.schedule.table.domain.model

/**
 * Ячейка логической таблицы расписания.
 *
 * Хранит координаты, текст и параметры растяжения по строкам/столбцам.
 *
 * @property row Номер строки.
 * @property column Номер столбца.
 * @property text Текст содержимого.
 * @property rowSpan Растяжение по строкам.
 * @property columnSpan Растяжение по столбцам.
 */
class ScheduleTableCell(
    val row: Int,
    val column: Int,
    val text: String,
    val rowSpan: Int,
    val columnSpan: Int
)