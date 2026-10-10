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
import org.joda.time.DateTime
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * Сервис для загрузки расписания из Moodle.
 *
 * Отвечает за скачивание PDF-файлов расписания и их парсинг.
 * Использует Jsoup для HTTP-запросов и управления cookies (аналогично MoodleRemoteService).
 *
 * @property context Контекст приложения.
 * @property parserUseCase UseCase для парсинга PDF-файлов.
 */
class MoodleLoaderService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val parserUseCase: ParserUseCase
) : ScheduleLoaderService {

    private companion object {
        const val TAG = "MoodleLoaderService"
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        const val LOGIN_URL = "https://edu.stankin.ru/login/index.php"
        const val COURSE_URL = "https://edu.stankin.ru/course/view.php?id=11557"
        const val TIMEOUT_MS = 60_000
        const val HTTP_OK = 200
        const val MAX_BODY_SIZE_BYTES = 50 * 1024 * 1024 // 50 MB
        const val ERROR_BODY_PREVIEW_LENGTH = 500
        val HTTP_REDIRECT_CODES = listOf(302, 303)
    }

    /**
     * Загружает и парсит расписание для указанной категории.
     *
     * @param category Категория расписания.
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

    /**
     * Выполняет гостевой вход на Moodle через Jsoup.
     *
     * @param cookies Карта cookies для сессии (обновляется на месте).
     * @return true если логин успешен.
     */
    private fun performGuestLogin(cookies: MutableMap<String, String>): Boolean {
        return try {
            Log.d(TAG, "Starting guest login via Jsoup...")

            // Шаг 1: Загрузить страницу логина
            val loginPageResponse = Jsoup.connect(LOGIN_URL)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .execute()

            cookies.putAll(loginPageResponse.cookies())
            val loginDoc = loginPageResponse.parse()

            val loginToken = loginDoc.select("input[name=logintoken]").attr("value")
            val loginAction = loginDoc.select("form[action*='login/index.php']").attr("action")
                .ifEmpty { LOGIN_URL }

            Log.d(TAG, "Login token found: ${loginToken.isNotEmpty()}, action: $loginAction")

            // Шаг 2: POST гостевой логин БЕЗ автоматического следования редиректам
            val loginConnection = Jsoup.connect(loginAction)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .data("username", "guest")
                .data("password", "guest")
                .cookies(cookies)
                .method(Connection.Method.POST)
                .followRedirects(false)

            if (loginToken.isNotEmpty()) {
                loginConnection.data("logintoken", loginToken)
            }

            val loginResponse = loginConnection.execute()
            cookies.putAll(loginResponse.cookies())
            Log.d(TAG, "Login POST response code: ${loginResponse.statusCode()}")

            // Шаг 3: Следовать по редиректу вручную
            if (loginResponse.statusCode() in HTTP_REDIRECT_CODES) {
                val location = loginResponse.header("Location")
                if (location != null) {
                    Log.d(TAG, "Login redirect to: $location")

                    val redirectResponse = Jsoup.connect(location)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT_MS)
                        .cookies(cookies)
                        .execute()
                    cookies.putAll(redirectResponse.cookies())
                    Log.d(TAG, "Redirect response code: ${redirectResponse.statusCode()}")

                    // Шаг 4: Посетить страницу курса для установки контекста сессии
                    val courseResponse = Jsoup.connect(COURSE_URL)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT_MS)
                        .cookies(cookies)
                        .execute()
                    cookies.putAll(courseResponse.cookies())
                    Log.d(TAG, "Course page response code: ${courseResponse.statusCode()}")
                    Log.d(TAG, "Cookies after login: ${cookies.keys}")

                    true
                } else {
                    Log.e(TAG, "Login redirect failed: Location header is null")
                    false
                }
            } else {
                Log.e(TAG, "Login failed with code: ${loginResponse.statusCode()} (expected 303)")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed with exception", e)
            false
        }
    }

    /**
     * Скачивает файл из Moodle с гостевой авторизацией через Jsoup.
     *
     * Алгоритм:
     *  1. Гостевой логин → получение cookies.
     *  2. Скачивание файла с этими cookies.
     *  3. При неудаче – повторный логин и попытка.
     *
     * @param url URL файла для скачивания.
     * @param fileName Имя для сохранения файла (без расширения).
     * @return Скачанный файл.
     */
    private suspend fun downloadFileInternal(url: String, fileName: String): File =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Starting download for: $url")

            val cookies = mutableMapOf<String, String>()
            val loginSuccess = performGuestLogin(cookies)
            Log.d(TAG, "Guest login result: $loginSuccess")

            // Первая попытка скачивания
            var downloadResponse = try {
                Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .cookies(cookies)
                    .header("Referer", "https://edu.stankin.ru/")
                    .ignoreContentType(true)
                    .maxBodySize(MAX_BODY_SIZE_BYTES) // 50 MB
                    .execute()
            } catch (e: Exception) {
                Log.w(TAG, "First download attempt failed with exception: ${e.message}")
                null
            }

            Log.d(TAG, "First attempt - code: ${downloadResponse?.statusCode()}, type: ${downloadResponse?.contentType()}")

            // Повторная попытка при ошибке или HTML-ответе
            if (downloadResponse == null
                || downloadResponse.statusCode() != HTTP_OK
                || downloadResponse.contentType()?.contains("text/html") == true
            ) {
                Log.d(TAG, "First attempt failed. Retrying with fresh login...")
                cookies.clear()
                performGuestLogin(cookies)

                downloadResponse = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .cookies(cookies)
                    .header("Referer", "https://edu.stankin.ru/")
                    .ignoreContentType(true)
                    .maxBodySize(MAX_BODY_SIZE_BYTES)
                    .execute()

                Log.d(TAG, "Retry - code: ${downloadResponse.statusCode()}, type: ${downloadResponse.contentType()}")
            }

            if (downloadResponse.statusCode() != HTTP_OK) {
                val bodyPreview = try { downloadResponse.body().take(ERROR_BODY_PREVIEW_LENGTH) } catch (_: Exception) { "N/A" }
                Log.e(TAG, "Download failed. Code: ${downloadResponse.statusCode()}, Body: $bodyPreview")
                throw IOException("Failed to download schedule. Response code: ${downloadResponse.statusCode()}")
            }

            val ct = downloadResponse.contentType() ?: ""
            if (ct.contains("text/html")) {
                val bodyPreview = try { downloadResponse.body().take(ERROR_BODY_PREVIEW_LENGTH) } catch (_: Exception) { "N/A" }
                Log.e(TAG, "Got HTML instead of PDF: $bodyPreview")
                throw IOException("Server returned HTML instead of PDF file. Login may have failed.")
            }

            // Сохраняем файл
            val downloadsDir = File(context.filesDir, "schedule_downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val file = File(downloadsDir, "${fileName}.pdf")
            FileOutputStream(file).use { output ->
                output.write(downloadResponse.bodyAsBytes())
            }

            Log.d(TAG, "File downloaded successfully: ${file.absolutePath} (${file.length()} bytes)")
            return@withContext file
        }
}
