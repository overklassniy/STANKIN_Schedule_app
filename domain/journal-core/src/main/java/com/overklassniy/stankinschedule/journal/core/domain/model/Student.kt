package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Модель студента.
 *
 * Содержит основные данные о студенте, полученные из журнала.
 *
 * @property name ФИО студента.
 * @property group Группа студента.
 * @property semesters Список семестров, доступных для студента (по возрастанию).
 */
class Student(
    val name: String,
    val group: String,
    val semesters: List<JournalSemester>,
) {
    override fun toString(): String {
        return "Student(name='$name', group='$group', semesters=$semesters)"
    }
}
