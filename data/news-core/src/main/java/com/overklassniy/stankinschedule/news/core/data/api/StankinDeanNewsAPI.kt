package com.overklassniy.stankinschedule.news.core.data.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API для получения новостей деканата с old.stankin.ru
 */
interface StankinDeanNewsAPI {

    @POST("/api_entry.php")
    fun getNewsResponse(@Body data: PostData): Call<OldNewsResponse>

    companion object {
        const val BASE_URL = "https://old.stankin.ru"

        /**
         * Запрос к API новостей old.stankin.ru (деканат).
         * @param api API для получения данных.
         * @param page номер страницы.
         * @param count количество новостей.
         */
        fun getNews(
            api: StankinDeanNewsAPI,
            page: Int,
            count: Int = 9,
        ): Call<OldNewsResponse> {
            val data = mapOf(
                "count" to count,
                "page" to page,
                "is_main" to false,
                "pull_site" to false,
                "subdivision_id" to 125,
                "tag" to "",
                "query_search" to ""
            )
            return api.getNewsResponse(PostData("getNews", data))
        }

        @Keep
        class PostData(
            @SerializedName("action") val action: String,
            @SerializedName("data") val data: Map<String, Any>,
        )
    }
}

/**
 * Ответ с новостями от old.stankin.ru
 */
data class OldNewsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: OldNewsData,
    @SerializedName("error") val error: String?,
) {
    data class OldNewsData(
        @SerializedName("news") val news: List<OldNewsItem>,
        @SerializedName("count") val count: Int,
    )

    data class OldNewsItem(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("date") val date: String,
        @SerializedName("logo") val logo: String?,
        @SerializedName("short_text") val shortText: String?,
        @SerializedName("author_id") val authorId: Int?,
    )
}
