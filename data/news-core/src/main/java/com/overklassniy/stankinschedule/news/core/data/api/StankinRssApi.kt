package com.overklassniy.stankinschedule.news.core.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * API для получения RSS-лент новостей с сайта stankin.ru.
 *
 * Используется для загрузки RSS XML, который затем парсится в `RssItem`.
 * Конкретные URL формируются из BuildConfig полей.
 */
interface StankinRSS {

    /**
     * Загружает RSS-ленту по произвольному URL.
     *
     * @param url Полный URL RSS-ленты (включая параметры LIMIT).
     * @return [Call] с содержимым RSS в виде XML-строки.
     */
    @GET
    fun getRssFeed(@Url url: String): Call<String>

    companion object {
        /**
         * Базовый URL для Retrofit (не используется напрямую, т.к. URL передается через @Url).
         */
        const val BASE_URL = "https://stankin.ru"
    }
}