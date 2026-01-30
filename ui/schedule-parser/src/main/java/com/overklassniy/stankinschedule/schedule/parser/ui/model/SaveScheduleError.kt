package com.overklassniy.stankinschedule.schedule.parser.ui.model

/**
 * Ошибки сохранения расписания.
 *
 * Описывает причины отказа при сохранении.
 */
sealed interface SaveScheduleError {

    /** Некорректное имя расписания */
    object InvalidScheduleName : SaveScheduleError

    /** Имя расписания уже существует */
    object ScheduleNameAlreadyExists : SaveScheduleError
}