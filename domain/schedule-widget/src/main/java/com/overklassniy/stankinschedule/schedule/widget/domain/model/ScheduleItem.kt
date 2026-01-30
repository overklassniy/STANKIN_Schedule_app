package com.overklassniy.stankinschedule.schedule.widget.domain.model

/**
 * Элемент выбора расписания для виджета.
 *
 * @property scheduleName Название расписания.
 * @property scheduleId Идентификатор расписания.
 */
data class ScheduleItem(
    val scheduleName: String,
    val scheduleId: Long
) {
    companion object {
        /**
         * Специальное значение, обозначающее отсутствие выбранного расписания.
         */
        val NO_ITEM = ScheduleItem("", -1)
    }
}