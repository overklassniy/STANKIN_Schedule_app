package com.overklassniy.stankinschedule.core.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

/**
 * API для работы с GitHub Releases.
 */
interface GitHubApi {

    @GET("repos/overklassniy/STANKIN_Schedule_app/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}

/**
 * Модель ответа GitHub Release API.
 */
data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    val name: String,
    val body: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("published_at") val publishedAt: String
)
