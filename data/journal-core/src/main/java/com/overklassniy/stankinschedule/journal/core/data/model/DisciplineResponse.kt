package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера с данными по дисциплине.
 *
 * Отсутствующие (null) оценки означают, что контрольная точка
 * по данному типу не применяется или оценка еще не выставлена.
 *
 * @param disciplineName Название дисциплины.
 * @param module1 Оценка за первый модуль.
 * @param module2 Оценка за второй модуль.
 * @param coursework Оценка за курсовую работу.
 * @param credit Оценка за зачет.
 * @param exam Оценка за экзамен.
 * @param coefficient Коэффициент значимости дисциплины.
 */
class DisciplineResponse(
    @SerializedName("disciplineName") val disciplineName: String,
    @SerializedName("module1") val module1: Int? = null,
    @SerializedName("module2") val module2: Int? = null,
    @SerializedName("coursework") val coursework: Int? = null,
    @SerializedName("credit") val credit: Int? = null,
    @SerializedName("exam") val exam: Int? = null,
    @SerializedName("coefficient") val coefficient: Double? = null,
)
