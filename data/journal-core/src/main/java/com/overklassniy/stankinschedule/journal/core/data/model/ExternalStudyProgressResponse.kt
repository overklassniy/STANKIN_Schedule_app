package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера с данными успеваемости студента.
 * Эндпоинт: `lkstudent/api/externalStudyProgress`.
 *
 * @param rating Рейтинг студента по семестрам.
 * @param accumulatedRating Накопленный рейтинг по семестрам.
 * @param data Основные данные успеваемости (студент и оценки за период).
 * @param allowedSemesters Список семестров, доступных для просмотра.
 */
class ExternalStudyProgressResponse(
    @SerializedName("rating") val rating: List<Double> = emptyList(),
    @SerializedName("accumulatedRating") val accumulatedRating: List<Double> = emptyList(),
    @SerializedName("data") val data: StudyProgressData? = null,
    @SerializedName("allowedSemesters") val allowedSemesters: List<AllowedSemesterResponse> = emptyList(),
)
