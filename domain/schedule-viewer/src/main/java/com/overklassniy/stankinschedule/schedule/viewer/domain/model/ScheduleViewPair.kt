package com.overklassniy.stankinschedule.schedule.viewer.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

data class ScheduleViewPair(
    val id: Long,
    val title: String,
    val lecturer: String,
    val classroom: ViewContent,
    val subgroup: Subgroup,
    val type: Type,
    val startTime: String,
    val endTime: String
)