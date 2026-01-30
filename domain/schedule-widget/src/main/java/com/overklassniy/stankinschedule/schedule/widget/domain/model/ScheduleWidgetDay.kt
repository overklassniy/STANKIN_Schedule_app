package com.overklassniy.stankinschedule.schedule.widget.domain.model

import org.joda.time.LocalDate


/**
 * День в данных виджета расписания.
 *
 * @property day Название дня недели.
 * @property date Дата дня.
 * @property pairs Список пар для дня.
 */
data class ScheduleWidgetDay(
    val day: String,
    val date: LocalDate,
    val pairs: List<ScheduleWidgetPair>
)