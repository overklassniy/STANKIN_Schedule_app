package com.overklassniy.stankinschedule.news.core.domain.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для локального хранения списка новостей (кэш).
 */
interface NewsStorageRepository {

    /**
     * Создает источник данных для постраничной загрузки новостей из локальной базы.
     *
     * @param newsSubdivision Идентификатор подразделения новостей.
     * @return [PagingSource] с ключом [Int] (номер страницы/позиция) и значением [NewsPost].
     */
    fun news(newsSubdivision: Int): PagingSource<Int, NewsPost>

    /**
     * Возвращает поток с последними новостями (для виджетов или превью).
     *
     * @param newsCount Количество новостей для загрузки (по умолчанию 3).
     * @return [Flow] со списком последних [NewsPost].
     */
    fun lastNews(newsCount: Int = 3): Flow<List<NewsPost>>

    /**
     * Возвращает поток с последними новостями конкретного подразделения.
     *
     * @param newsSubdivision Идентификатор подразделения.
     * @param newsCount Количество новостей для загрузки (по умолчанию 3).
     * @return [Flow] со списком последних [NewsPost].
     */
    fun lastNews(newsSubdivision: Int, newsCount: Int = 3): Flow<List<NewsPost>>

    /**
     * Сохраняет список новостей в локальное хранилище.
     *
     * @param newsSubdivision Идентификатор подразделения.
     * @param posts Список новостей для сохранения.
     * @param force Если `true`, очищает старые записи перед сохранением.
     */
    suspend fun saveNews(newsSubdivision: Int, posts: List<NewsPost>, force: Boolean = false)

    /**
     * Очищает кэш новостей для указанного подразделения.
     *
     * @param newsSubdivision Идентификатор подразделения.
     */
    suspend fun clearNews(newsSubdivision: Int)
}