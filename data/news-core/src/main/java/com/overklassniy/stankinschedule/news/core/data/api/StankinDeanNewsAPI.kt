package com.overklassniy.stankinschedule.news.core.data.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Интерфейс API для получения новостей деканата с сайта old.stankin.ru.
 * Объединяет функциональность получения списка новостей и содержимого конкретной новости.
 * Использует Retrofit для выполнения HTTP-запросов.
 */
interface StankinDeanNewsAPI {

    /**
     * Выполняет POST-запрос к эндпоинту /api_entry.php для получения списка новостей.
     *
     * @param data Объект [PostData], содержащий параметры запроса (действие и данные).
     * @return Объект [Call] с ответом [OldNewsResponse], содержащим список новостей.
     */
    @POST("/api_entry.php")
    fun getNewsResponse(@Body data: PostData): Call<OldNewsResponse>

    /**
     * Выполняет POST-запрос к эндпоинту /api_entry.php для получения содержимого новости.
     *
     * @param data Объект [PostData], содержащий параметры запроса (действие и данные).
     * @return Объект [Call] с ответом [PostResponse], содержащим контент новости.
     */
    @POST("/api_entry.php")
    fun getPostResponse(@Body data: PostData): Call<PostResponse>

    companion object {
        const val BASE_URL = "https://old.stankin.ru"

        /**
         * Вспомогательная функция для формирования запроса списка новостей.
         *
         * @param api Экземпляр [StankinDeanNewsAPI] для выполнения запроса.
         * @param page Номер страницы для загрузки (пагинация).
         * @param count Количество новостей на странице (по умолчанию 9).
         * @return Объект [Call] с ответом сервера.
         */
        fun getNews(
            api: StankinDeanNewsAPI,
            page: Int,
            count: Int = 9,
        ): Call<OldNewsResponse> {
            // Формирование параметров запроса для old.stankin.ru
            val data = mapOf(
                "count" to count,
                "page" to page,
                "is_main" to false,
                "pull_site" to false,
                "subdivision_id" to 125, // ID деканата или общего раздела новостей
                "tag" to "",
                "query_search" to ""
            )
            return api.getNewsResponse(PostData("getNews", data))
        }

        /**
         * Вспомогательная функция для получения содержимого новости по ID.
         *
         * @param api Экземпляр [StankinDeanNewsAPI] для выполнения запроса.
         * @param newsId ID новости.
         * @return Объект [Call] с ответом сервера.
         */
        fun getNewsPost(
            api: StankinDeanNewsAPI,
            newsId: Int,
        ): Call<PostResponse> {
            val data = mapOf(
                "id" to newsId
            )
            return api.getPostResponse(
                PostData(
                    "getNewsItem",
                    data
                )
            )
        }

        /**
         * Модель данных для тела POST-запроса.
         * Используется для передачи действия и параметров на сервер.
         *
         * @param action Название действия (например, "getNews").
         * @param data Карта с параметрами запроса.
         */
        @Keep
        class PostData(
            // Используется Gson для сериализации в JSON (отправляется на сервер)
            @SerializedName("action") val action: String,
            @SerializedName("data") val data: Map<String, Any>,
        )
    }
}

/**
 * Модель ответа сервера с новостями от old.stankin.ru.
 *
 * @param success Признак успешного выполнения запроса.
 * @param data Объект с данными ответа (список новостей и общее количество).
 * @param error Сообщение об ошибке (если есть).
 */
data class OldNewsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: OldNewsData,
    @SerializedName("error") val error: String?,
) {

    /**
     * Данные ответа новостей.
     *
     * @param news Список элементов новостей [OldNewsItem].
     * @param count Общее количество новостей (для пагинации).
     */
    data class OldNewsData(
        @SerializedName("news") val news: List<OldNewsItem>,
        @SerializedName("count") val count: Int,
    )

    /**
     * Элемент новости.
     */
    data class OldNewsItem(
        @SerializedName("id") val id: Int,
        @SerializedName("date") val date: String,
        @SerializedName("title") val title: String,
        @SerializedName("logo") val logo: String?,
        @SerializedName("text") val text: String?,
    )
}