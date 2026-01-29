package com.overklassniy.stankinschedule.schedule.repository.data.repository

import android.content.Context
import android.util.Log
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.overklassniy.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.joda.time.DateTime
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Сервис для загрузки расписания из Moodle.
 *
 * Отвечает за скачивание PDF-файлов расписания и их парсинг.
 *
 * @property context Контекст приложения.
 * @property parserUseCase UseCase для парсинга PDF-файлов.
 */
class MoodleLoaderService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val parserUseCase: ParserUseCase
) : ScheduleLoaderService {

    private val TAG = "MoodleLoaderService"
    private val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private val LOGIN_URL = "https://edu.stankin.ru/login/index.php"

    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            cookieStore.getOrPut(host) { mutableListOf() }.apply {
                cookies.forEach { newCookie ->
                    removeAll { it.name == newCookie.name }
                }
                addAll(cookies)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .proxy(Proxy.NO_PROXY)
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addNetworkInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            if (response.code == 407) {
                return@addNetworkInterceptor response.newBuilder()
                    .code(401)
                    .message("Unauthorized (Rewritten from 407)")
                    .build()
            }
            response
        }
        .build()

    /**
     * Загружает и парсит расписание для указанной категории.
     *
     * @param category Категория расписания (не используется в текущей реализации, но может быть полезна в будущем).
     * @param schedule URL или путь к файлу расписания для скачивания.
     * @return Список пар [PairModel], полученных в результате парсинга.
     */
    override suspend fun schedule(category: String, schedule: String): List<PairModel> =
        withContext(Dispatchers.IO) {
            val tempFile = downloadFileInternal(schedule, "schedule_import")

            val currentYear = DateTime.now().year
            val settings = ParserSettings(
                scheduleYear = currentYear,
                parserThreshold = 0.5f
            )

            val results = parserUseCase.parsePDF(tempFile.absolutePath, settings)

            val pairs = mutableListOf<PairModel>()
            for (result in results) {
                if (result is ParseResult.Success) {
                    pairs.add(result.pair)
                }
            }

            tempFile.delete()

            return@withContext pairs
        }

    /**
     * Скачивает файл расписания и сохраняет его локально.
     *
     * @param category Категория расписания.
     * @param schedule URL файла для скачивания.
     * @param fileName Имя файла для сохранения (без расширения).
     * @return Абсолютный путь к сохраненному файлу.
     */
    override suspend fun downloadScheduleFile(
        category: String,
        schedule: String,
        fileName: String
    ): String = withContext(Dispatchers.IO) {
        val file = downloadFileInternal(schedule, fileName)
        return@withContext file.absolutePath
    }

    private fun performGuestLogin(): Boolean {
        try {
            Log.d(TAG, "Starting guest login...")

            val loginPageRequest = Request.Builder()
                .url(LOGIN_URL)
                .header("User-Agent", USER_AGENT)
                .build()

            val loginPageResponse = client.newCall(loginPageRequest).execute()
            val loginPageHtml = loginPageResponse.body.string()
            loginPageResponse.close()

            val doc = Jsoup.parse(loginPageHtml)
            val loginToken = doc.select("input[name=logintoken]").attr("value")
            val loginAction = doc.select("form[action*='login/index.php']").attr("action")
                .ifEmpty { LOGIN_URL }

            Log.d(TAG, "Login token found: ${loginToken.isNotEmpty()}, action: $loginAction")

            val formBody = FormBody.Builder()
                .add("username", "guest")
                .add("password", "guest")
                .apply {
                    if (loginToken.isNotEmpty()) {
                        add("logintoken", loginToken)
                    }
                }
                .build()

            val loginRequest = Request.Builder()
                .url(loginAction)
                .header("User-Agent", USER_AGENT)
                .header("Referer", LOGIN_URL)
                .post(formBody)
                .build()

            val loginResponse = client.newCall(loginRequest).execute()
            val loginSuccess = loginResponse.isSuccessful ||
                    loginResponse.code == 303 ||
                    loginResponse.code == 302
            Log.d(TAG, "Login response code: ${loginResponse.code}, success: $loginSuccess")
            Log.d(TAG, "Cookies after login: ${cookieStore.values.flatten().map { it.name }}")
            loginResponse.close()

            return loginSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Login failed with exception", e)
            return false
        }
    }

    private suspend fun downloadFileInternal(url: String, fileName: String): File =
        withContext(Dispatchers.IO) {
            cookieStore.clear()
            Log.d(TAG, "Starting download for: $url")

            val loginSuccess = performGuestLogin()
            Log.d(TAG, "Guest login result: $loginSuccess")

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://edu.stankin.ru/")
                .build()

            var response = client.newCall(request).execute()
            Log.d(
                TAG,
                "First download attempt - code: ${response.code}, url: ${response.request.url}"
            )

            val finalUrl = response.request.url.toString()
            val contentType = response.header("Content-Type") ?: ""

            if (finalUrl.contains("/login/") || contentType.contains("text/html")) {
                Log.d(TAG, "Got HTML or login redirect, trying again after fresh login...")
                response.close()

                cookieStore.clear()
                performGuestLogin()

                val retryRequest = Request.Builder()
                    .url(url)
                    .header("User-Agent", USER_AGENT)
                    .header("Referer", "https://edu.stankin.ru/")
                    .build()

                response = client.newCall(retryRequest).execute()
                Log.d(TAG, "Retry download - code: ${response.code}, url: ${response.request.url}")
            }

            if (!response.isSuccessful) {
                val errorBody = response.body.string().take(500)
                Log.e(TAG, "Download failed. Code: ${response.code}, Body preview: $errorBody")
                response.close()
                throw Exception("Failed to download schedule. Response code: ${response.code}")
            }

            val body = response.body

            val responseContentType = response.header("Content-Type") ?: ""
            if (responseContentType.contains("text/html")) {
                val htmlPreview = body.string().take(500)
                Log.e(TAG, "Got HTML instead of PDF: $htmlPreview")
                throw Exception("Server returned HTML instead of PDF file. Login may have failed.")
            }

            val downloadsDir = File(context.filesDir, "schedule_downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val file = File(downloadsDir, "${fileName}.pdf")
            FileOutputStream(file).use { output ->
                body.byteStream().use { input ->
                    input.copyTo(output)
                }
            }

            Log.d(TAG, "File downloaded successfully: ${file.absolutePath}")
            return@withContext file
        }
}
