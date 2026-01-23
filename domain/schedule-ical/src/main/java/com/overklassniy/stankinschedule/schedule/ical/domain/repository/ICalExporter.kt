package com.overklassniy.stankinschedule.schedule.ical.domain.repository

import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalCalendar

interface ICalExporter {

    suspend fun export(calendar: ICalCalendar, path: String)

}