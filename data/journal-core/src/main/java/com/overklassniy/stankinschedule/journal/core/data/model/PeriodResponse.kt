package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Данные успеваемости за один учебный период (семестр).
 *
 * @param semester Название периода (например, "2 курс 1 семестр").
 * @param rating Рейтинг за период.
 * @param accumulatedRating Накопленный рейтинг на конец периода.
 * @param disciplines Список дисциплин с оценками.
 */
class PeriodResponse(
    @SerializedName("semester") val semester: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("accumulatedRating") val accumulatedRating: Double? = null,
    @SerializedName("disciplines") val disciplines: List<DisciplineResponse> = emptyList(),
)
