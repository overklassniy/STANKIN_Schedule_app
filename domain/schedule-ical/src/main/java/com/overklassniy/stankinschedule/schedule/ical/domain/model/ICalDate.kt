package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Абстрактный базовый класс для дат в формате iCal.
 *
 * @property startTime Время начала события.
 * @property endTime Время окончания события.
 */
abstract class ICalDate(
    val startTime: String,
    val endTime: String
)