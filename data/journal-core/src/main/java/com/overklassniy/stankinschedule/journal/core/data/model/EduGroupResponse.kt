package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера с учебной группой студента.
 * Эндпоинт: `lkstudent/studedugroup/GetStudEduGroup/`.
 *
 * @param eduGroup Данные учебной группы.
 */
class EduGroupResponse(
    @SerializedName("eduGroup") val eduGroup: EduGroup? = null,
)

/**
 * Учебная группа студента.
 *
 * @param groupName Название группы (например, "ИДБ-24-04").
 */
class EduGroup(
    @SerializedName("groupName") val groupName: String? = null,
)
