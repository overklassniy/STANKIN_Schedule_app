package com.overklassniy.stankinschedule.news.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
import com.overklassniy.stankinschedule.news.core.data.api.StankinUniversityNewsAPI
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import retrofit2.await
import javax.inject.Inject

/**
 * Реализация удаленного репозитория для загрузки новостей.
 * Поддерживает загрузку новостей как с основного сайта университета (парсинг HTML),
 * так и с сайта деканата (через API).
 *
 * @property universityAPI API для работы с сайтом университета (получение HTML).
 * @property deanAPI API для работы с сайтом деканата (JSON API).
 */
class UniversityNewsRepositoryImpl @Inject constructor(
    private val universityAPI: StankinUniversityNewsAPI,
    private val deanAPI: StankinDeanNewsAPI,
) : NewsRemoteRepository {

    /**
     * Загружает страницу новостей для указанного подразделения.
     *
     * @param newsSubdivision ID подразделения (например, университет или деканат).
     * @param page Номер страницы для загрузки (начиная с 1).
     * @param count Количество новостей на странице (используется только для API деканата).
     * @return Список загруженных новостей [NewsPost].
     */
    override suspend fun loadPage(
        newsSubdivision: Int,
        page: Int,
        count: Int
    ): List<NewsPost> {
        return when (newsSubdivision) {
            NewsSubdivision.Dean.id -> loadDeanNews(page, count)
            NewsSubdivision.Announcements.id -> loadAnnouncementsPage(page)
            else -> loadUniversityNews(page)
        }
    }

    /**
     * Загружает и парсит новости университета с сайта stankin.ru/news.
     * Использует регулярные выражения для извлечения данных из HTML.
     *
     * @param page Номер страницы для загрузки.
     * @return Список новостей университета.
     */
    private suspend fun loadUniversityNews(page: Int): List<NewsPost> {
        val text = universityAPI.getNewsPage(page).await()

        val newsBlock = Regex(
            """<a.{0,200}class="(newsItem|importantNewsItem)".*?>.+?</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val newsTitle = Regex("""class="name".*?>(.+?)<""", RegexOption.DOT_MATCHES_ALL)

        val newsImage = Regex("""class="imgW".*?src="(.+?)"""", RegexOption.DOT_MATCHES_ALL)
        val importantNewsImage = Regex("""url\((.+?)\)""", RegexOption.DOT_MATCHES_ALL)

        val newsDate =
            Regex("""<span.*?class="date".*?>(.+?)</span>""", RegexOption.DOT_MATCHES_ALL)
        val newsLink = Regex("""href="(.+?)"""", RegexOption.DOT_MATCHES_ALL)

        return newsBlock.findAll(text)
            .asFlow()
            .map { match -> match.value }
            .map { block ->
                NewsPost(
                    id = 0, // ID для новостей университета генерируется позже (в базе данных)
                    title = newsTitle.find(block)
                        .getOrThrow(1),
                    previewImageUrl = (newsImage.find(block) ?: importantNewsImage.find(block))
                        ?.let { StankinUniversityNewsAPI.BASE_URL + it.groupValues[1] },
                    date = processDate(
                        newsDate.find(block)
                            .getOrThrow(1)
                    ),
                    relativeUrl = newsLink.find(block)
                        ?.let { StankinUniversityNewsAPI.BASE_URL + it.groupValues[1] }
                )
            }
            .catch {
                Log.e("UniversityNewsRepo", "Load university news page $page error", it)
            }
            .toList()
            .also { Log.d("UniversityNewsRepo", "loadUniversityNews: $it") }
    }

    /**
     * Загружает новости деканата через API old.stankin.ru.
     *
     * @param page Номер страницы.
     * @param count Количество новостей на странице.
     * @return Список новостей деканата.
     */
    private suspend fun loadDeanNews(page: Int, count: Int): List<NewsPost> {
        return try {
            val response = StankinDeanNewsAPI.getNews(deanAPI, page, count).await()

            if (!response.success) {
                Log.e("UniversityNewsRepo", "Dean API error: ${response.error}")
                return emptyList()
            }

            response.data.news.map { item ->
                NewsPost(
                    id = item.id,
                    title = item.title,
                    previewImageUrl = item.logo?.let { logo ->
                        if (logo.startsWith("http")) logo
                        else StankinDeanNewsAPI.BASE_URL + logo
                    },
                    date = processDeanDate(item.date),
                    relativeUrl = "${StankinDeanNewsAPI.BASE_URL}/news/item_${item.id}"
                )
            }.also { Log.d("UniversityNewsRepo", "loadDeanNews: $it") }
        } catch (e: Exception) {
            Log.e("UniversityNewsRepo", "Load Dean news page $page error", e)
            emptyList()
        }
    }

    /**
     * Загружает анонсы с сайта stankin.ru/ads/.
     * Для первой страницы загружает также вторую (PAGEN_1=2), чтобы на главном экране было больше анонсов.
     *
     * @param page Номер страницы (1-based).
     * @return Список анонсов.
     */
    private suspend fun loadAnnouncementsPage(page: Int): List<NewsPost> {
        return if (page == 1) {
            loadAnnouncements(1) + loadAnnouncements(2)
        } else {
            loadAnnouncements(page)
        }
    }

    /**
     * Загружает одну страницу анонсов с сайта stankin.ru/ads/.
     *
     * @param page Номер страницы (PAGEN_1).
     * @return Список анонсов.
     */
    private suspend fun loadAnnouncements(page: Int): List<NewsPost> {
        return try {
            val text = universityAPI.getAdsPage(page).await()

            // Паттерн для блока анонса: <a href="/ads/..." class="nucAds ">...</a>
            // Порядок атрибутов: href потом class
            val adsBlock = Regex(
                """<a\s+[^>]*?href="(/ads/[^"]+/)"[^>]*?>(.*?)</a>""",
                setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
            )

            // Паттерн для изображения
            val adsImage = Regex("""<img[^>]*src="([^"]+)"[^>]*>""", RegexOption.DOT_MATCHES_ALL)

            // Паттерн для даты: <span class="date">22/01<br>2026</span>
            val adsDate = Regex(
                """<span[^>]*class="date"[^>]*>(\d{1,2}/\d{1,2})<br>(\d{4})</span>""",
                RegexOption.DOT_MATCHES_ALL
            )

            // Паттерн для заголовка: <span class="name">...</span>
            val adsTitle = Regex(
                """<span[^>]*class="name"[^>]*>([^<]+)</span>""",
                RegexOption.DOT_MATCHES_ALL
            )

            adsBlock.findAll(text)
                .asFlow()
                .map { match ->
                    val link = match.groupValues[1]
                    val content = match.groupValues[2]

                    val imageUrl = adsImage.find(content)?.let {
                        StankinUniversityNewsAPI.BASE_URL + it.groupValues[1]
                    }

                    val dateMatch = adsDate.find(content)
                    val dateStr = if (dateMatch != null) {
                        processAdsDate(dateMatch.groupValues[1], dateMatch.groupValues[2])
                    } else {
                        DateTime.now().toString(ISODateTimeFormat.date())
                    }

                    val title = adsTitle.find(content)?.groupValues?.get(1)
                        ?.trim()
                        ?.replace(Regex("\\s+"), " ")
                        ?: "Анонс"

                    NewsPost(
                        id = 0,
                        title = title,
                        previewImageUrl = imageUrl,
                        date = dateStr,
                        relativeUrl = StankinUniversityNewsAPI.BASE_URL + link
                    )
                }
                .catch {
                    Log.e("UniversityNewsRepo", "Load announcements page $page error", it)
                }
                .toList()
                .also { Log.d("UniversityNewsRepo", "loadAnnouncements: count=${it.size}") }
        } catch (e: Exception) {
            Log.e("UniversityNewsRepo", "Load announcements page $page error", e)
            emptyList()
        }
    }

    /**
     * Преобразует дату анонса в формат ISO.
     *
     * @param dayMonth Строка с днем и месяцем (например, "22/01").
     * @param year Строка с годом (например, "2026").
     * @return Дата в формате yyyy-MM-dd.
     */
    private fun processAdsDate(dayMonth: String, year: String): String {
        return try {
            DateTimeFormat.forPattern("dd/MM/yyyy")
                .parseDateTime("$dayMonth/$year")
                .toString(ISODateTimeFormat.date())
        } catch (_: Throwable) {
            DateTime.now().toString(ISODateTimeFormat.date())
        }
    }

    /**
     * Преобразует строку даты с сайта университета в формат ISO.
     *
     * @param text Строка с датой (например, "dd/MM/yyyy" или с HTML-тегами).
     * @return Дата в формате yyyy-MM-dd или текущая дата в случае ошибки.
     */
    private fun processDate(text: String): String {
        return try {
            text
                .replace("<br>", "/")
                .replace("\"", "")
                .trim()
                .let {
                    DateTimeFormat.forPattern("dd/MM/yyyy")
                        .parseDateTime(it)
                        .toString(ISODateTimeFormat.date())
                }
        } catch (_: Throwable) {
            DateTime.now()
                .toString(ISODateTimeFormat.date())
        }
    }

    /**
     * Преобразует строку даты из API деканата в формат ISO.
     *
     * @param date Строка с датой (например, "dd.MM.yyyy HH:mm:ss" или "yyyy-MM-dd").
     * @return Дата в формате yyyy-MM-dd или текущая дата в случае ошибки.
     */
    private fun processDeanDate(date: String): String {
        return try {
            // Формат даты от old.stankin.ru: "20.01.2025 17:22:52" или "2025-01-20"
            val cleanDate = date.split(" ").first()

            if (cleanDate.contains("-")) {
                // Уже в ISO формате
                cleanDate
            } else {
                // Формат dd.MM.yyyy
                DateTimeFormat.forPattern("dd.MM.yyyy")
                    .parseDateTime(cleanDate)
                    .toString(ISODateTimeFormat.date())
            }
        } catch (_: Throwable) {
            DateTime.now()
                .toString(ISODateTimeFormat.date())
        }
    }

    /**
     * Расширение для безопасного извлечения группы из результата поиска регулярного выражения.
     *
     * @param index Индекс группы для извлечения.
     * @return Найденная подстрока.
     * @throws NoSuchElementException Если совпадение не найдено (null).
     */
    private fun MatchResult?.getOrThrow(index: Int): String {
        if (this == null) throw NoSuchElementException("Match is null")
        return groupValues[index]
    }
}