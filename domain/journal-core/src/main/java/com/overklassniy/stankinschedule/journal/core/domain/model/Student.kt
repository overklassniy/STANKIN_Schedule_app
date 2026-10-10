package com.overklassniy.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Модель студента.
 *
 * Содержит основные данные о студенте, полученные из журнала.
 *
 * Аннотации [SerializedName] обязательны: без них R8 вырезает generic-сигнатуры
 * полей из dex, и Gson не сможет десериализовать [semesters] из кэша.
 *
 * @property name ФИО студента.
 * @property group Группа студента.
 * @property semesters Список семестров, доступных для студента (по убыванию — от новых к старым).
 */
class Student(
    @SerializedName("name") val name: String,
    @SerializedName("group") val group: String,
    @SerializedName("semesters") val semesters: List<JournalSemester>,
) {
    override fun toString(): String {
        return "Student(name='$name', group='$group', semesters=$semesters)"
    }
}
