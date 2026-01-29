package com.overklassniy.stankinschedule.core.domain.cache

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Контейнер для кэширования данных.
 *
 * Обертка над данными [data], содержащая время кэширования [cacheTime].
 * Используется для хранения результатов запросов с меткой времени для определения актуальности.
 *
 * @param T Тип хранимых данных.
 * @property data Сами данные, которые необходимо закэшировать.
 * @property cacheTime Время, когда данные были сохранены в кэш.
 */
class CacheContainer<T : Any>(
    @SerializedName("data") val data: T,
    @SerializedName("time") val cacheTime: DateTime,
) {

    /**
     * Возвращает данные из контейнера.
     * Позволяет использовать деструктуризацию: `val (data, time) = cacheContainer`.
     *
     * @return Данные типа [T].
     */
    operator fun component1(): T = data

    /**
     * Возвращает время кэширования.
     * Позволяет использовать деструктуризацию: `val (data, time) = cacheContainer`.
     *
     * @return Время кэширования [DateTime].
     */
    operator fun component2(): DateTime = cacheTime

    /**
     * Возвращает строковое представление контейнера.
     *
     * @return Строка с содержимым данных и временем кэширования.
     */
    override fun toString(): String {
        return "CacheContainer(data=$data, cacheTime=$cacheTime)"
    }
}