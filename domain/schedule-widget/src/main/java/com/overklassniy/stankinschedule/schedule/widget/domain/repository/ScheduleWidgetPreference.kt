package com.overklassniy.stankinschedule.schedule.widget.domain.repository

import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData

interface ScheduleWidgetPreference {

    fun loadData(appWidgetId: Int): ScheduleWidgetData?

    fun saveData(appWidgetId: Int, data: ScheduleWidgetData)

    fun deleteData(appWidgetId: Int)
}