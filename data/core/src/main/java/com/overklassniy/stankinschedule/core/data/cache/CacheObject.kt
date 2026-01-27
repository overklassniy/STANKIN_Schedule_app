package com.overklassniy.stankinschedule.core.data.cache

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Объект для хранения кэшированных данных.
 * Используется для сериализации и десериализации данных кэша вместе с меткой времени.
 *
 * @param data Сами данные в формате JsonElement
 * @param time Время создания кэша
 */
class CacheObject(
    // Данные кэша в формате JSON
    @SerializedName("data") val data: JsonElement?,

    // Время, когда данные были сохранены в кэш
    @SerializedName("time") val time: DateTime?,
)