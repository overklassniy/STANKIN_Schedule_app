package com.overklassniy.stankinschedule.schedule.ical.domain.model

class ICalRecurrenceDate(
    startTime: String,
    endTime: String,
    val frequency: ICalFrequency,
    val untilDate: String,
    val byDay: ICalDayOfWeek
) : ICalDate(startTime, endTime)