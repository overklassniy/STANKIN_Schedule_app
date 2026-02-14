package com.overklassniy.stankinschedule.news.core.data.repository

import android.util.Base64
import android.util.Log
import com.overklassniy.stankinschedule.news.core.data.BuildConfig
import com.overklassniy.stankinschedule.news.core.data.api.StankinRSS
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import retrofit2.await
import javax.inject.Inject

/**
 * Реализация удалённого репозитория новостей через RSS-ленты.
 *
 * Все категории новостей загружаются из RSS-лент сайта stankin.ru:
 * - Университет, Деканат, Аспирантура → NEWS RSS (фильтрация по category)
 * - Анонсы → ADS RSS
 * - Международная деятельность → EXCHANGE RSS
 * @property rssApi API для загрузки RSS-лент.
 */
class UniversityNewsRepositoryImpl @Inject constructor(
    private val rssApi: StankinRSS,
) : NewsRemoteRepository {

    private val TAG = "UniversityNewsRepo"

    /**
     * Деобфусцирует строку, закодированную через XOR + Base64 на этапе сборки.
     * Должна соответствовать функции obfuscate() из build.gradle.kts.
     */
    private fun deobfuscate(input: String, key: Int = 0x5A): String {
        if (input.isBlank()) return ""
        val decoded = Base64.decode(input, Base64.DEFAULT)
        return String(decoded.map { (it.toInt() xor key).toByte() }.toByteArray(), Charsets.UTF_8)
    }

    /**
     * Кэш распарсенных RSS-элементов новостей (NEWS feed).
     * Используется повторно для University, Dean и PhD,
     * чтобы не загружать один и тот же фид три раза.
     */
    private var newsFeedCache: List<RssItem>? = null

    /**
     * Загружает страницу новостей для указанного подразделения.
     *
     * @param newsSubdivision ID подразделения (например, университет или деканат).
     * @param page Номер страницы для загрузки (начиная с 1). Для RSS используется только page=1.
     * @param count Количество новостей (не используется для RSS, ограничение через LIMIT в URL).
     * @return Список загруженных новостей [NewsPost].
     */
    override suspend fun loadPage(
        newsSubdivision: Int,
        page: Int,
        count: Int
    ): List<NewsPost> {
        return when (newsSubdivision) {
            NewsSubdivision.University.id -> loadFromNewsFeed("Главные новости")
            NewsSubdivision.Dean.id -> loadFromNewsFeed("Деканат")
            NewsSubdivision.PhD.id -> loadFromNewsFeed("Аспирантура")
            NewsSubdivision.Announcements.id -> loadRssFeed(deobfuscate(BuildConfig.ADS_RSS_URL), null)
            NewsSubdivision.Exchange.id -> loadRssFeed(deobfuscate(BuildConfig.EXCHANGE_RSS_URL), null)
            else -> emptyList()
        }
    }

    /**
     * Загружает и фильтрует новости из NEWS RSS-ленты по категории.
     * Результат NEWS-ленты кэшируется, чтобы повторные вызовы для разных категорий
     * не генерировали дополнительные сетевые запросы.
     *
     * @param categoryFilter Строка категории для фильтрации (например, "Главные новости").
     * @return Отфильтрованный список [NewsPost].
     */
    private suspend fun loadFromNewsFeed(categoryFilter: String): List<NewsPost> {
        return try {
            val items = getNewsFeedItems()
            val filtered = RssParser.filterByCategory(items, categoryFilter)
            Log.d(TAG, "loadFromNewsFeed($categoryFilter): ${filtered.size} items from ${items.size} total")
            filtered.map { it.toNewsPost() }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading news feed for '$categoryFilter'", e)
            emptyList()
        }
    }

    /**
     * Загружает все элементы NEWS RSS-ленты (с кэшированием).
     */
    private suspend fun getNewsFeedItems(): List<RssItem> {
        newsFeedCache?.let { return it }

        val url = deobfuscate(BuildConfig.NEWS_RSS_URL)
        if (url.isBlank()) {
            Log.e(TAG, "NEWS_RSS_URL is empty, stankin.secret may be missing")
            return emptyList()
        }

        val xml = rssApi.getRssFeed(url).await()
        val items = RssParser.parse(xml)
        newsFeedCache = items
        Log.d(TAG, "NEWS feed loaded: ${items.size} items")
        return items
    }

    /**
     * Загружает RSS-ленту по URL с опциональной фильтрацией по категории.
     *
     * @param feedUrl URL RSS-ленты.
     * @param categoryFilter Фильтр категории (null — без фильтрации).
     * @return Список [NewsPost].
     */
    private suspend fun loadRssFeed(feedUrl: String, categoryFilter: String?): List<NewsPost> {
        if (feedUrl.isBlank()) {
            Log.e(TAG, "RSS URL is empty, stankin.secret may be missing")
            return emptyList()
        }

        return try {
            val xml = rssApi.getRssFeed(feedUrl).await()
            var items = RssParser.parse(xml)

            if (categoryFilter != null) {
                items = RssParser.filterByCategory(items, categoryFilter)
            }

            Log.d(TAG, "loadRssFeed($feedUrl): ${items.size} items")
            items.map { it.toNewsPost() }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading RSS feed: $feedUrl", e)
            emptyList()
        }
    }

    /**
     * Сбрасывает кэш NEWS-ленты.
     * Вызывается при принудительном обновлении.
     */
    override fun invalidateCache() {
        newsFeedCache = null
    }

    /**
     * Преобразует [RssItem] в доменную модель [NewsPost].
     */
    private fun RssItem.toNewsPost(): NewsPost {
        return NewsPost(
            id = 0,
            title = title,
            previewImageUrl = imageUrl,
            date = pubDate,
            relativeUrl = link
        )
    }
}