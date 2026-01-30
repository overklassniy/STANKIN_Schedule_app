package com.overklassniy.stankinschedule.schedule.viewer.ui.components

/**
 * Форматы экспорта расписания.
 *
 * Назначение: определяет тип выходного файла при сохранении.
 *
 * @property memeType MIME тип, который используется для сохранения и открытия файла.
 * Инварианты: значение соответствует зарегистрированным MIME типам.
 */
enum class ExportFormat(val memeType: String) {
    /** JSON представление расписания. Удобно для импорта в приложение. */
    Json("application/json"),

    /** iCalendar (.ics). Подходит для импорта в календарные клиенты. */
    ICal("text/calendar");
}