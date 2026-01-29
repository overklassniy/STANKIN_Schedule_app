package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Представляет одиночную дату события в формате iCal.
 *
 * @property startTime Время начала события.
 * @property endTime Время окончания события.
 */
class ICalSingleDate(
    startTime: String,
    endTime: String,
) : ICalDate(startTime, endTime)