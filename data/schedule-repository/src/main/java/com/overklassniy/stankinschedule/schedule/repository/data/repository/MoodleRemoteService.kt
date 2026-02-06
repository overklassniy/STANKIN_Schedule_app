package com.overklassniy.stankinschedule.schedule.repository.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryCategory
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.jsoup.Connection
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Сервис для взаимодействия с удаленным репозиторием расписаний (Moodle).
 *
 * Отвечает за получение описания репозитория и списка файлов расписания.
 */
class MoodleRemoteService @Inject constructor() : ScheduleRemoteService {

    private val moodleUrl = "https://edu.stankin.ru/course/view.php?id=11557"
    private val loginUrl = "https://edu.stankin.ru/login/index.php"
    private val TAG = "MoodleRemoteService"
    private val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private val TIMEOUT_MS = 60_000

    /**
     * Получает описание репозитория расписаний.
     *
     * Определяет текущий учебный год на основе текущей даты.
     *
     * @return Объект [RepositoryDescription] с информацией об обновлении и категориях.
     */
    override suspend fun description(): RepositoryDescription = withContext(Dispatchers.IO) {
        val now = DateTime.now()
        val academicYearStart = if (now.monthOfYear >= 9) now.year else now.year - 1

        return@withContext RepositoryDescription(
            lastUpdate = "Moodle",
            categories = listOf(
                RepositoryCategory("Расписание", academicYearStart)
            )
        )
    }

    /**
     * Получает список элементов репозитория (файлов расписания) для указанной категории.
     *
     * Выполняет скрапинг сайта Moodle:
     * 1. Авторизуется как гость.
     * 2. Находит разделы курса (Бакалавриат, Специалитет, Магистратура, Аспирантура).
     * 3. В каждом разделе находит папки с расписанием.
     * 4. Извлекает ссылки на PDF-файлы расписаний из папок.
     *
     * @param category Категория расписания.
     * @return Список объектов [RepositoryItem], содержащих название и ссылку на файл.
     * @throws Exception Если произошла ошибка при загрузке или парсинге страниц.
     */
    override suspend fun category(category: String): List<RepositoryItem> =
        withContext(Dispatchers.IO) {
            val items = mutableListOf<RepositoryItem>()
            Log.d(TAG, "Starting category scraping for: $category")

            val cookies = mutableMapOf<String, String>()

            try {
                // Шаг 1: Подключение и авторизация
                Log.d(TAG, "Connecting to Moodle URL: $moodleUrl")
                val response = Jsoup.connect(moodleUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .execute()

                cookies.putAll(response.cookies())
                var doc = response.parse()
                Log.d(TAG, "Initial connection successful. Title: ${doc.title()}")

                if (doc.title().contains("Вход") || doc.select(".login-form").isNotEmpty()) {
                    Log.d(TAG, "Login page detected. Attempting guest login...")
                    doc = performGuestLogin(doc, cookies)
                    Log.d(TAG, "Page title after login flow: ${doc.title()}")
                }

                // Шаг 2: Поиск разделов курса
                // Разделы доступны через ссылки вида course/section.php?id=...
                // Названия: Бакалавриат, Специалитет, Магистратура, Аспирантура
                val sectionLinks = doc.select("a[href*='course/section.php']")
                val scheduleSections = mutableListOf<Pair<String, String>>()
                val sectionKeywords = listOf(
                    "Бакалавриат", "Специалитет", "Магистратура", "Аспирантура"
                )

                for (link in sectionLinks) {
                    val text = link.text().trim()
                    if (sectionKeywords.any { text.contains(it, ignoreCase = true) }) {
                        val href = link.attr("href")
                        // Избегаем дублирования ссылок
                        if (scheduleSections.none { it.second == href }) {
                            scheduleSections.add(text to href)
                            Log.d(TAG, "Schedule section found: $text -> $href")
                        }
                    }
                }
                Log.d(TAG, "Found ${scheduleSections.size} schedule sections")

                // Если разделы не найдены, пробуем старый метод поиска папок на главной странице
                if (scheduleSections.isEmpty()) {
                    Log.d(TAG, "No sections found, falling back to direct folder search...")
                    val folderUrls = findRelevantFolders(doc)
                    for (folderUrl in folderUrls) {
                        extractScheduleFiles(folderUrl, category, cookies, items)
                    }
                } else {
                    // Шаг 3: Обход каждого раздела
                    for ((sectionName, sectionUrl) in scheduleSections) {
                        try {
                            Log.d(TAG, "Processing section: $sectionName -> $sectionUrl")
                            val sectionResponse = Jsoup.connect(sectionUrl)
                                .userAgent(USER_AGENT)
                                .timeout(TIMEOUT_MS)
                                .cookies(cookies)
                                .execute()

                            cookies.putAll(sectionResponse.cookies())
                            val sectionDoc = sectionResponse.parse()

                            // Ищем папки внутри раздела
                            val folderLinks =
                                sectionDoc.select("a[href*='mod/folder/view.php']")
                            Log.d(
                                TAG,
                                "Section '$sectionName' has ${folderLinks.size} folder links"
                            )

                            for (folderLink in folderLinks) {
                                val folderText = folderLink.text().trim()
                                val folderHref = folderLink.attr("href")

                                // Пропускаем списки групп
                                if (folderText.contains(
                                        "Списки групп",
                                        ignoreCase = true
                                    )
                                ) {
                                    Log.d(TAG, "Skipped group list folder: $folderText")
                                    continue
                                }

                                Log.d(TAG, "Processing folder: $folderText -> $folderHref")
                                extractScheduleFiles(folderHref, category, cookies, items)
                            }

                            // Также ищем прямые ссылки на ресурсы/файлы внутри раздела
                            // (для случаев, когда файлы лежат не в папках, а как отдельные
                            // ресурсы, например Расписание занятий)
                            val directFolders =
                                sectionDoc.select("a[href*='mod/folder/view.php']")
                            for (link in directFolders) {
                                val text = link.text().trim()
                                if (text.contains("Расписание", ignoreCase = true)) {
                                    val href = link.attr("href")
                                    Log.d(TAG, "Direct schedule folder: $text -> $href")
                                    extractScheduleFiles(href, category, cookies, items)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing section $sectionName", e)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Fatal error in scraping", e)
                throw Exception("Failed to load schedule from Moodle: ${e.message}", e)
            }

            Log.d(TAG, "Scraping finished. Total items found: ${items.size}")
            return@withContext items
        }

    /**
     * Выполняет гостевую авторизацию на Moodle.
     *
     * @param loginDoc Документ страницы входа.
     * @param cookies Cookies текущей сессии (обновляются в процессе).
     * @return Документ страницы после авторизации.
     */
    private fun performGuestLogin(
        loginDoc: org.jsoup.nodes.Document,
        cookies: MutableMap<String, String>
    ): org.jsoup.nodes.Document {
        val loginForm = loginDoc.select("form[action*='login/index.php']").first()
        val loginAction = loginForm?.attr("action") ?: loginUrl
        val loginToken =
            loginForm?.select("input[name=logintoken]")?.attr("value") ?: ""
        Log.d(TAG, "Login token extracted: ${if (loginToken.isNotEmpty()) "YES" else "NO"}")

        Log.d(TAG, "Sending login POST request...")
        val loginResponse = Jsoup.connect(loginAction)
            .userAgent(USER_AGENT)
            .timeout(TIMEOUT_MS)
            .data("username", "guest")
            .data("password", "guest")
            .apply {
                if (loginToken.isNotEmpty()) {
                    data("logintoken", loginToken)
                }
            }
            .cookies(cookies)
            .method(Connection.Method.POST)
            .followRedirects(false)
            .execute()

        cookies.putAll(loginResponse.cookies())
        Log.d(TAG, "Login response code: ${loginResponse.statusCode()}")

        if (loginResponse.statusCode() == 303 || loginResponse.statusCode() == 302) {
            val location = loginResponse.header("Location")
            if (location != null) {
                Log.d(TAG, "Login successful (Redirect). Location: $location")

                val redirectResponse = Jsoup.connect(location)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .cookies(cookies)
                    .execute()

                cookies.putAll(redirectResponse.cookies())

                // После редиректа возвращаемся на страницу курса
                val courseResponse = Jsoup.connect(moodleUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .cookies(cookies)
                    .execute()
                cookies.putAll(courseResponse.cookies())
                return courseResponse.parse()
            } else {
                Log.e(TAG, "Login redirect failed: Location header is null")
            }
        } else {
            Log.e(TAG, "Login failed. Status code: ${loginResponse.statusCode()}")
            val errDoc = loginResponse.parse()
            val errorMsg = errDoc.select(".error").text()
            if (errorMsg.isNotEmpty()) Log.e(TAG, "Moodle Error: $errorMsg")
        }

        // Возвращаем страницу курса
        val courseResponse = Jsoup.connect(moodleUrl)
            .userAgent(USER_AGENT)
            .timeout(TIMEOUT_MS)
            .cookies(cookies)
            .execute()
        cookies.putAll(courseResponse.cookies())
        return courseResponse.parse()
    }

    /**
     * Находит папки с расписанием на странице (fallback метод).
     */
    private fun findRelevantFolders(doc: org.jsoup.nodes.Document): List<String> {
        val folderLinks = doc.select("a[href*='mod/folder/view.php']")
        val relevantFolders = mutableListOf<String>()

        for (link in folderLinks) {
            val text = link.text()
            val isExam = text.contains("Экзамен", ignoreCase = true) ||
                    text.contains("Зачет", ignoreCase = true)
            val isGroupList = text.contains("Списки групп", ignoreCase = true)

            if ((text.contains("Расписание", ignoreCase = true) ||
                        text.contains("курс", ignoreCase = true))
                && !isExam && !isGroupList
            ) {
                val href = link.attr("href")
                relevantFolders.add(href)
                Log.d(TAG, "Relevant folder found: $text -> $href")
            }
        }
        return relevantFolders
    }

    /**
     * Извлекает файлы расписания из папки Moodle.
     *
     * @param folderUrl URL папки.
     * @param category Категория расписания.
     * @param cookies Cookies текущей сессии.
     * @param items Список для добавления найденных элементов.
     */
    private fun extractScheduleFiles(
        folderUrl: String,
        category: String,
        cookies: MutableMap<String, String>,
        items: MutableList<RepositoryItem>
    ) {
        try {
            val folderResponse = Jsoup.connect(folderUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .cookies(cookies)
                .execute()

            cookies.putAll(folderResponse.cookies())
            val folderDoc = folderResponse.parse()

            val fileLinks = folderDoc.select("a")
            var filesInFolder = 0
            val scheduleRegex = Regex("^[А-Яа-яA-Za-z]+-\\d+-\\d+.*")

            for (link in fileLinks) {
                val href = link.attr("href")
                if (href.contains("forcedownload=1") ||
                    href.contains("/mod/resource/view.php") ||
                    href.contains("pluginfile.php")
                ) {
                    val text = link.text()
                    if (text.contains("Скачать папку", ignoreCase = true)) continue

                    var name = text.replace(" Файл", "").replace(" File", "").trim()
                    if (name.endsWith(".pdf", ignoreCase = true)) {
                        name = name.substringBeforeLast(".")
                    }

                    val isSchedule = name.matches(scheduleRegex) && !name.contains(
                        "Экзамен",
                        ignoreCase = true
                    )

                    if (name.isNotEmpty() && isSchedule) {
                        // Избегаем дублирования
                        if (items.none { it.name == name }) {
                            items.add(RepositoryItem(name, href, category))
                            filesInFolder++
                            Log.d(TAG, "File found: $name -> $href")
                        }
                    } else {
                        Log.d(TAG, "Skipped file: $name")
                    }
                }
            }
            Log.d(TAG, "Found $filesInFolder files in folder: $folderUrl")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing folder $folderUrl", e)
        }
    }
}