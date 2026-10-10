package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Идентификатор доступного семестра из ответа API.
 *
 * @param course Номер курса.
 * @param semester Номер семестра внутри курса.
 */
class AllowedSemesterResponse(
    @SerializedName("course") val course: Int,
    @SerializedName("semester") val semester: Int,
)
