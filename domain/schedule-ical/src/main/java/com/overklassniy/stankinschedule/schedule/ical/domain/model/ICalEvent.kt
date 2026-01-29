package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Представляет событие в календаре iCal.
 *
 * @property summory Краткое описание (заголовок) события.
 * @property description Полное описание события.
 * @property location Место проведения события.
 * @property date Информация о дате и времени события.
 */
class ICalEvent(
    val summory: String,
    val description: String,
    val location: String,
    val date: ICalDate
)