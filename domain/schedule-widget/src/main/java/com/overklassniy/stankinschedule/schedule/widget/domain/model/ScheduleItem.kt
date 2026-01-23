package com.overklassniy.stankinschedule.schedule.widget.domain.model

data class ScheduleItem(
    val scheduleName: String,
    val scheduleId: Long
) {
    companion object {
        val NO_ITEM = ScheduleItem("", -1)
    }
}