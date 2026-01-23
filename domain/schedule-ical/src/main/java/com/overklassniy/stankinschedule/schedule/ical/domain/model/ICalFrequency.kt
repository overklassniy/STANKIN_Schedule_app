package com.overklassniy.stankinschedule.schedule.ical.domain.model

sealed interface ICalFrequency {
    class ICalWeekly(val interval: Int) : ICalFrequency
}