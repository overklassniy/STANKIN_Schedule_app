package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Представляет повторяющуюся дату события в формате iCal.
 *
 * @property startTime Время начала события.
 * @property endTime Время окончания события.
 * @property frequency Частота повторения события.
 * @property untilDate Дата окончания повторений.
 * @property byDay День недели, в который происходит событие.
 */
class ICalRecurrenceDate(
    startTime: String,
    endTime: String,
    val frequency: ICalFrequency,
    val untilDate: String,
    val byDay: ICalDayOfWeek
) : ICalDate(startTime, endTime)