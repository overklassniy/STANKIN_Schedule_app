package com.overklassniy.stankinschedule.schedule.ical.domain.model

/**
 * Перечисление дней недели для формата iCal.
 *
 * @property tag Строковое представление дня недели в формате iCal (например, "MO", "TU").
 */
enum class ICalDayOfWeek(val tag: String) {
    @Suppress("unused")
    SU("SU"),
    MO("MO"),
    TU("TU"),
    WE("WE"),
    TH("TH"),
    FR("FR"),
    SA("SA");
}