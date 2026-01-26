package com.overklassniy.stankinschedule.news.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.news.core.data.api.StankinNews2024API
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
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

class NewsRemoteRepository2024Impl @Inject constructor(
    private val newsAPI: StankinNews2024API,
    private val oldNewsAPI: StankinDeanNewsAPI,
) : NewsRemoteRepository {

    override suspend fun loadPage(
        newsSubdivision: Int,
        page: Int,
        count: Int
    ): List<NewsPost> {
        return when (newsSubdivision) {
            NewsSubdivision.Deanery.id -> loadDeaneryNews(page, count)
            else -> loadUniversityNews(page)
        }
    }

    /**
     * Загружает новости университета с stankin.ru/news
     */
    private suspend fun loadUniversityNews(page: Int): List<NewsPost> {
        val text = newsAPI.getNewsPage(page).await()

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
                    id = 0,
                    title = newsTitle.find(block)
                        .getOrThrow(1),
                    previewImageUrl = (newsImage.find(block) ?: importantNewsImage.find(block))
                        ?.let { StankinNews2024API.BASE_URL + it.groupValues[1] },
                    date = processDate(
                        newsDate.find(block)
                            .getOrThrow(1)
                    ),
                    relativeUrl = newsLink.find(block)
                        ?.let { StankinNews2024API.BASE_URL + it.groupValues[1] }
                )
            }
            .catch {
                Log.e("NewsRemoteRepository2024Impl", "Load university news page $page error", it)
            }
            .toList()
            .also { Log.d("NewsRemoteRepository2024Impl", "loadUniversityNews: $it") }
    }

    /**
     * Загружает новости деканата с old.stankin.ru
     */
    private suspend fun loadDeaneryNews(page: Int, count: Int): List<NewsPost> {
        return try {
            val response = StankinDeanNewsAPI.getNews(oldNewsAPI, page, count).await()
            
            if (!response.success) {
                Log.e("NewsRemoteRepository2024Impl", "Deanery API error: ${response.error}")
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
                    date = processDeaneryDate(item.date),
                    relativeUrl = "${StankinDeanNewsAPI.BASE_URL}/news/item_${item.id}"
                )
            }.also { Log.d("NewsRemoteRepository2024Impl", "loadDeaneryNews: $it") }
        } catch (e: Exception) {
            Log.e("NewsRemoteRepository2024Impl", "Load deanery news page $page error", e)
            emptyList()
        }
    }

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
        } catch (ignored: Throwable) {
            DateTime.now()
                .toString(ISODateTimeFormat.date())
        }
    }

    private fun processDeaneryDate(date: String): String {
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
        } catch (ignored: Throwable) {
            DateTime.now()
                .toString(ISODateTimeFormat.date())
        }
    }

    private fun MatchResult?.getOrThrow(index: Int): String {
        if (this == null) throw NoSuchElementException("Match is null")
        return groupValues[index]
    }
}