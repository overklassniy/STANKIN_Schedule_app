package com.overklassniy.stankinschedule.core.data.api

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
    val tag_name: String,
    val name: String,
    val body: String,
    val html_url: String,
    val published_at: String
)