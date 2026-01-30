package com.overklassniy.stankinschedule.schedule.widget.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup

/**
 * Данные для конфигурации виджета расписания.
 *
 * @property scheduleName Название расписания.
 * @property scheduleId Идентификатор расписания.
 * @property subgroup Подгруппа для отображения.
 * @property display Флаг отображения виджета.
 */
data class ScheduleWidgetData(
    val scheduleName: String,
    val scheduleId: Long,
    val subgroup: Subgroup,
    val display: Boolean,
)
