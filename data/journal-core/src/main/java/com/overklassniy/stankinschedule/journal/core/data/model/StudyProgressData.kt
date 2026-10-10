package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Данные успеваемости студента из ответа API.
 *
 * @param studentNumber Номер студенческого билета.
 * @param fio ФИО студента.
 * @param period Данные успеваемости за запрошенный период.
 * @param dateUploading Дата и время последней выгрузки данных.
 */
class StudyProgressData(
    @SerializedName("studentNumber") val studentNumber: String? = null,
    @SerializedName("fio") val fio: String? = null,
    @SerializedName("period") val period: PeriodResponse? = null,
    @SerializedName("dateUploading") val dateUploading: String? = null,
)
