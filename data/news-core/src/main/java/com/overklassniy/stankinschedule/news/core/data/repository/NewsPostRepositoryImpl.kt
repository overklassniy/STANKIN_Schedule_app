package com.overklassniy.stankinschedule.news.core.data.repository

import com.overklassniy.stankinschedule.core.data.cache.CacheManager
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
import com.overklassniy.stankinschedule.news.core.data.mapper.toNewsContent
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPostRepository
import retrofit2.await
import javax.inject.Inject

/**
 * Реализация репозитория для работы с контентом новостей.
 * Отвечает за загрузку полной информации о новости (текст, изображения) из сети и её кэширование.
 *
 * @property newsAPI API для загрузки новостей деканата.
 * @property cache Менеджер кэша для сохранения и загрузки данных.
 */
class NewsPostRepositoryImpl @Inject constructor(
    private val newsAPI: StankinDeanNewsAPI,
    private val cache: CacheManager,
) : NewsPostRepository {

    init {
        // Инициализация пути для кэширования новостей
        cache.addStartedPath("news_posts")
    }

    /**
     * Сохраняет контент новости в кэш.
     *
     * @param news Объект [NewsContent] для сохранения.
     */
    override suspend fun saveNewsContent(news: NewsContent) {
        cache.saveToCache(news, generateName(postId = news.id))
    }

    /**
     * Загружает контент новости из кэша.
     *
     * @param postId ID новости.
     * @return Контейнер [CacheContainer] с данными новости или null, если данных нет в кэше.
     */
    override suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>? {
        return cache.loadFromCache(NewsContent::class.java, generateName(postId = postId))
    }

    /**
     * Загружает контент новости из сети (через API деканата).
     *
     * @param postId ID новости.
     * @return Объект [NewsContent] с данными новости.
     */
    override suspend fun loadPost(postId: Int): NewsContent {
        val response = StankinDeanNewsAPI.getNewsPost(newsAPI, postId).await()
        return response.data.toNewsContent()
    }

    /**
     * Генерирует уникальное имя файла для кэширования новости.
     *
     * @param postId ID новости.
     * @return Строка с именем файла (например, "post_123").
     */
    private fun generateName(postId: Int): String = "post_$postId"
}