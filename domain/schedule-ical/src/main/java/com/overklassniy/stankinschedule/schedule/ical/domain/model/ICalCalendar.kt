package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Представляет календарь в формате iCal.
 *
 * @property name Имя календаря.
 * @property prodId Идентификатор продукта, создавшего календарь.
 * @property timeZone Временная зона календаря (например, "Europe/Moscow").
 * @property timeZoneName Название временной зоны (например, "MSK").
 * @property timeZoneOffset Смещение временной зоны (например, "+0300").
 * @property events Список событий календаря.
 */
class ICalCalendar(
    val name: String,
    val prodId: String = "-//Unknown//СТАНКИН Расписание//RU",
    val timeZone: String = "Europe/Moscow",
    val timeZoneName: String = "MSK",
    val timeZoneOffset: String = "+0300",
    val events: List<ICalEvent>
)