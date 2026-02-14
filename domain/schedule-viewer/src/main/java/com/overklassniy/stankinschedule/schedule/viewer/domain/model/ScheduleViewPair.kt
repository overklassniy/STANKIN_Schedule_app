package com.overklassniy.stankinschedule.schedule.viewer.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Представление пары для отображения в Viewer.
 *
 * @property id Идентификатор пары.
 * @property title Название дисциплины.
 * @property lecturer Преподаватель.
 * @property classroom Аудитория (как контент с возможным форматированием).
 * @property subgroup Подгруппа.
 * @property type Тип занятия.
 * @property startTime Время начала.
 * @property endTime Время окончания.
 * @property link Ссылка на занятие (необязательная).
 */
data class ScheduleViewPair(
    val id: Long,
    val title: String,
    val lecturer: String,
    val classroom: ViewContent,
    val subgroup: Subgroup,
    val type: Type,
    val startTime: String,
    val endTime: String,
    val link: String = ""
)