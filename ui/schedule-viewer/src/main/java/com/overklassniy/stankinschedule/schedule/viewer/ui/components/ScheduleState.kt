package com.overklassniy.stankinschedule.schedule.viewer.ui.components

/**
 * Состояние экрана просмотра расписания.
 *
 * - Loading — данные загружаются
 * - NotFound — расписание не найдено
 * - Success — данные загружены успешно
 *
 * В состоянии Success доступны поля:
 * @property scheduleName Имя расписания для заголовка.
 * @property isEmpty Признак пустого расписания.
 */
sealed interface ScheduleState {
    /** Данные загружаются. */
    object Loading : ScheduleState

    /** Расписание не найдено. */
    object NotFound : ScheduleState

    /** Успешная загрузка данных расписания. */
    class Success(val scheduleName: String, val isEmpty: Boolean) : ScheduleState
}
