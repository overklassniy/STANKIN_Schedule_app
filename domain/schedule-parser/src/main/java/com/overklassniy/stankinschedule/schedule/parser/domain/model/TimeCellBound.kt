package com.overklassniy.stankinschedule.schedule.parser.domain.model

/**
 * Представляет границы временной ячейки в расписании (столбец времени).
 *
 * @property startX Координата X начала временного слота.
 * @property endX Координата X конца временного слота.
 * @property startTime Строковое представление времени начала занятия (например, "08:30").
 * @property endTime Строковое представление времени окончания занятия (например, "10:10").
 */
class TimeCellBound(
    val startX: Float,
    val endX: Float,
    val startTime: String,
    val endTime: String
)