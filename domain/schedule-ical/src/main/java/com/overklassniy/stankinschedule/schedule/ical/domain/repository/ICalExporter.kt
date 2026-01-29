package com.overklassniy.stankinschedule.schedule.ical.domain.repository

import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalCalendar

/**
 * Интерфейс для экспорта календаря в формате iCal.
 */
interface ICalExporter {

    /**
     * Экспортирует календарь в файл.
     *
     * @param calendar Объект календаря [ICalCalendar] для экспорта.
     * @param path Путь к файлу, в который будет сохранен календарь.
     */
    suspend fun export(calendar: ICalCalendar, path: String)
}