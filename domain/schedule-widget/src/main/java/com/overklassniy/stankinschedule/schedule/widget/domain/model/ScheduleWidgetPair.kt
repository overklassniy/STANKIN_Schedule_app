package com.overklassniy.stankinschedule.schedule.widget.domain.model

/**
 * Пара для отображения в виджете расписания.
 *
 * @property title Название дисциплины.
 * @property classroom Аудитория.
 * @property time Время проведения.
 * @property type Тип пары для виджета.
 */
data class ScheduleWidgetPair(
    val title: String,
    val classroom: String,
    val time: String,
    val type: ScheduleWidgetPairType
)