package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Модель студента.
 *
 * Содержит основные данные о студенте, полученные из журнала.
 *
 * @property name ФИО студента.
 * @property group Группа студента.
 * @property semesters Список названий семестров, доступных для студента (например, "2023-2024 осень").
 */
class Student(
    val name: String,
    val group: String,
    val semesters: List<String>,
) {
    override fun toString(): String {
        return "Student(name='$name', group='$group', semesters=$semesters)"
    }
}