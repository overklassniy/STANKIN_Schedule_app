package com.overklassniy.stankinschedule.news.core.domain.repository

import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost

/**
 * Репозиторий для загрузки списка новостей с удаленного сервера.
 */
interface NewsRemoteRepository {

    /**
     * Загружает страницу новостей для указанного подразделения.
     *
     * @param newsSubdivision Идентификатор подразделения.
     * @param page Номер страницы для загрузки.
     * @param count Количество новостей на странице (по умолчанию 40).
     * @return Список загруженных [NewsPost].
     */
    suspend fun loadPage(newsSubdivision: Int, page: Int, count: Int = 40): List<NewsPost>
}