package com.overklassniy.stankinschedule.schedule.widget.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup

data class ScheduleWidgetData(
    val scheduleName: String,
    val scheduleId: Long,
    val subgroup: Subgroup,
    val display: Boolean,
)
