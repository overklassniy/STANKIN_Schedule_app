package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Интерфейс, описывающий частоту повторения события в iCal.
 */
sealed interface ICalFrequency {

    /**
     * Еженедельная частота повторения.
     *
     * @property interval Интервал повторения (в неделях).
     */
    class ICalWeekly(val interval: Int) : ICalFrequency
}