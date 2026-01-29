package com.overklassniy.stankinschedule.news.core.domain.usecase

import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * UseCase для просмотра содержимого новостей.
 *
 * Отвечает за загрузку детальной информации о новости с использованием кэширования.
 */
class NewsViewerUseCase @Inject constructor(
    private val repository: NewsPostRepository,
) {

    /**
     * Загружает контент новости по её идентификатору.
     *
     * Сначала проверяет наличие кэшированной версии. Если кэш существует и актуален (менее 24 часов),
     * возвращает данные из кэша. Иначе загружает данные с сервера и обновляет кэш.
     *
     * @param postId Идентификатор новости.
     * @param force Принудительная загрузка с сервера (игнорирование кэша).
     * @return [Flow] с объектом [NewsContent].
     */
    fun loadNewsContent(postId: Int, force: Boolean = false): Flow<NewsContent> = flow {

        val cache = repository.loadNewsContent(postId)
        if (cache != null && !force && (cache.cacheTime subHours DateTime.now() < 24)) {
            emit(cache.data)
            return@flow
        }

        val newsContent = repository.loadPost(postId)
        repository.saveNewsContent(newsContent)

        emit(newsContent)
    }
}