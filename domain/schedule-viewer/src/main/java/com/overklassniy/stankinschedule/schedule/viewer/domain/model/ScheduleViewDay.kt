package com.overklassniy.stankinschedule.schedule.viewer.domain.model

import org.joda.time.LocalDate

/**
 * День для отображения расписания в Viewer.
 *
 * @property pairs Список пар для дня.
 * @property day Дата дня.
 */
data class ScheduleViewDay(
    val pairs: List<ScheduleViewPair>,
    val day: LocalDate,
)