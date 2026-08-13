package com.overklassniy.stankinschedule.news.widget.domain.usecase

import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlinx.coroutines.flow.first
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * UseCase для получения данных новостей, необходимых Glance-виджету.
 *
 * Загружает последние новости для выбранных категорий, объединяет и сортирует по дате.
 * Конфигурация виджета (цвета, пресет, категории) хранится в Glance state и читается
 * непосредственно в NewsGlanceWidget.
 */
class NewsWidgetUseCase @Inject constructor(
    private val storageRepository: NewsStorageRepository,
) {

    /**
     * Загружает объединённый список последних новостей из выбранных категорий.
     *
     * Алгоритм:
     * 1. Для каждой выбранной категории загружает [count] последних новостей из локальной БД.
     * 2. Объединяет все списки.
     * 3. Сортирует по дате (убывание).
     * 4. Оставляет только [count] первых элементов.
     *
     * @param subdivisions Множество выбранных категорий новостей.
     * @param count Количество новостей в итоговом списке.
     * @return Список пар: категория и новость.
     */
    suspend fun lastNews(
        subdivisions: Set<NewsSubdivision>,
        count: Int = 10,
    ): List<Pair<NewsSubdivision, NewsPost>> {
        if (subdivisions.isEmpty()) return emptyList()

        val combined = mutableListOf<Pair<NewsSubdivision, NewsPost>>()

        for (subdivision in subdivisions) {
            val posts = storageRepository.lastNews(subdivision.id, count).first()
            posts.forEach { post ->
                combined.add(subdivision to post)
            }
        }

        return combined
            .sortedByDescending { parseDate(it.second.date) }
            .take(count)
    }

    /**
     * Парсит строку даты в [DateTime].
     *
     * @param dateStr Строка даты в формате RSS или ISO.
     * @return [DateTime] или [DateTime.now] в случае ошибки парсинга.
     */
    private fun parseDate(dateStr: String): DateTime {
        return try {
            DateTime.parse(dateStr)
        } catch (_: Exception) {
            try {
                org.joda.time.format.DateTimeFormat
                    .forPattern("EEE, dd MMM yyyy HH:mm:ss Z")
                    .withLocale(java.util.Locale.ENGLISH)
                    .parseDateTime(dateStr)
            } catch (_: Exception) {
                try {
                    org.joda.time.format.DateTimeFormat
                        .forPattern("EEE, dd MMM yyyy HH:mm:ss z")
                        .withLocale(java.util.Locale.ENGLISH)
                        .parseDateTime(dateStr)
                } catch (_: Exception) {
                    DateTime.now()
                }
            }
        }
    }
}