package com.overklassniy.stankinschedule.news.core.domain.repository

import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent

/**
 * Репозиторий для работы с детальным содержимым новостей (посты).
 *
 * Отвечает за загрузку полного текста новости и его кэширование.
 */
interface NewsPostRepository {

    /**
     * Сохраняет содержимое новости в кэш.
     *
     * @param news Объект [NewsContent] для сохранения.
     */
    suspend fun saveNewsContent(news: NewsContent)

    /**
     * Загружает кэшированное содержимое новости.
     *
     * @param postId Идентификатор новости.
     * @return Контейнер [CacheContainer] с данными новости или null, если кэш отсутствует.
     */
    suspend fun loadNewsContent(postId: Int): CacheContainer<NewsContent>?

    /**
     * Загружает содержимое новости с сервера.
     *
     * @param postId Идентификатор новости.
     * @return Загруженный объект [NewsContent].
     */
    suspend fun loadPost(postId: Int): NewsContent
}