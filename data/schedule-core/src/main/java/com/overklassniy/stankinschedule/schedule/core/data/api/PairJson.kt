package com.overklassniy.stankinschedule.schedule.core.data.api

import com.google.gson.annotations.SerializedName

/**
 * Модель пары (занятия) для парсинга JSON ответа API расписания.
 *
 * @property title Название предмета.
 * @property lecturer Имя преподавателя.
 * @property classroom Аудитория.
 * @property type Тип занятия (лекция, семинар и т.д.).
 * @property subgroup Подгруппа (A, B или пусто).
 * @property time Время проведения занятия.
 * @property date Список дат проведения занятия.
 * @property link Ссылка на занятие (необязательная).
 */
data class PairJson(
    @SerializedName("title") val title: String,
    @SerializedName("lecturer") val lecturer: String,
    @SerializedName("classroom") val classroom: String,
    @SerializedName("type") val type: String,
    @SerializedName("subgroup") val subgroup: String,
    @SerializedName("time") val time: TimeJson,
    @SerializedName("dates") val date: List<DateJson>,
    @SerializedName("link") val link: String = "",
) {
    /**
     * Модель времени проведения занятия.
     *
     * @property start Время начала занятия (HH:mm).
     * @property end Время окончания занятия (HH:mm).
     */
    data class TimeJson(
        @SerializedName("start") val start: String,
        @SerializedName("end") val end: String,
    )

    /**
     * Модель даты проведения занятия.
     *
     * @property frequency Частота проведения (once, every, through).
     * @property date Дата проведения (в различных форматах в зависимости от frequency).
     */
    data class DateJson(
        @SerializedName("frequency") val frequency: String,
        @SerializedName("date") val date: String,
    )
}