package com.overklassniy.stankinschedule.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.core.domain.repository.GooglePlayAvailabilityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val PLAY_STORE_APP_URL = "https://play.google.com/store/apps/details?id=com.overklassniy.stankinschedule"

/**
 * Проверяет доступность приложения в Google Play по HTTP-ответу страницы.
 * Приложение считается доступным, если ответ 200 и в теле страницы есть маркер страницы приложения.
 */
class GooglePlayAvailabilityRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : GooglePlayAvailabilityRepository {

    override suspend fun isAppAvailableOnPlayStore(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(PLAY_STORE_APP_URL)
                    .get()
                    .addHeader("User-Agent", USER_AGENT)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val code = response.code
                val body = response.body?.string() ?: ""
                response.close()

                if (code != 200) {
                    false
                } else {
                    // Страница приложения в Play Store содержит itemprop="name" в разметке
                    body.contains("itemprop=\"name\"", ignoreCase = true)
                }
            } catch (e: Exception) {
                Log.e("GooglePlayAvailability", "Failed to check Play Store availability", e)
                false
            }
        }
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
    }
}
