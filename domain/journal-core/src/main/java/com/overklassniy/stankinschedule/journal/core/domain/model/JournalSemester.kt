package com.overklassniy.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Идентификатор семестра в новом личном кабинете (lko.stankin.ru).
 *
 * API успеваемости адресует семестры парой чисел: курс и номер семестра на курсе.
 *
 * Аннотации [SerializedName] обязательны: они защищают имена полей от обфускации R8
 * при десериализации из кэша журнала.
 *
 * @property course Номер курса (начиная с 1).
 * @property semester Номер семестра внутри курса (1 — осенний, 2 — весенний).
 */
data class JournalSemester(
    @SerializedName("course") val course: Int,
    @SerializedName("semester") val semester: Int,
) : Comparable<JournalSemester> {

    /**
     * Стабильный ключ для кэширования оценок семестра.
     */
    fun cacheKey(): String = "semester_${course}_$semester"

    override fun compareTo(other: JournalSemester): Int {
        return compareValuesBy(this, other, { it.course }, { it.semester })
    }

    override fun toString(): String = "$course курс $semester семестр"
}
