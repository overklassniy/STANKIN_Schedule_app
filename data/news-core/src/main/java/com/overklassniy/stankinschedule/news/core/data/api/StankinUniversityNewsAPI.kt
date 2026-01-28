package com.overklassniy.stankinschedule.news.core.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API для работы с новостями с основного сайта университета (stankin.ru).
 *
 * Используется для получения HTML-страниц с новостями, которые затем парсятся вручную.
 */
interface StankinUniversityNewsAPI {

    /**
     * Получает HTML-страницу со списком новостей.
     *
     * @param page Номер страницы для пагинации (соответствует параметру запроса PAGEN_1).
     * @return [Call] с содержимым страницы в виде HTML-строки.
     */
    @GET("/news")
    fun getNewsPage(@Query("PAGEN_1") page: Int): Call<String>

    /**
     * Получает HTML-страницу со списком анонсов.
     *
     * @param page Номер страницы для пагинации.
     * @return [Call] с содержимым страницы в виде HTML-строки.
     */
    @GET("/ads/")
    fun getAdsPage(@Query("PAGEN_1") page: Int): Call<String>

    companion object {
        /**
         * Базовый URL основного сайта университета.
         */
        const val BASE_URL = "https://stankin.ru"
    }
}