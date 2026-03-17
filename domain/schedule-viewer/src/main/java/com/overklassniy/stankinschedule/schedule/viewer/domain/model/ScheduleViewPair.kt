package com.overklassniy.stankinschedule.schedule.viewer.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Информация о преподавателе для модального окна (подразделения, почта).
 *
 * @property departments Список подразделений.
 * @property email E-mail.
 */
data class LecturerInfo(
    val departments: List<String>,
    val email: String,
) {
    val departmentsDisplay: String
        get() = departments.joinToString(", ")
}

/**
 * Представление пары для отображения в Viewer.
 *
 * @property id Идентификатор пары.
 * @property title Название дисциплины.
 * @property lecturer Преподаватель (ФИО при наличии в справочнике, иначе как в расписании).
 * @property lecturerInfo Информация для модального окна при клике (подразделения, почта).
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
    val lecturerInfo: LecturerInfo? = null,
    val classroom: ViewContent,
    val subgroup: Subgroup,
    val type: Type,
    val startTime: String,
    val endTime: String,
    val link: String = ""
)