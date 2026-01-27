package com.overklassniy.stankinschedule.news.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.overklassniy.stankinschedule.news.core.data.source.NewsRemoteSource
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsMediatorRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import javax.inject.Inject

/**
 * Реализация репозитория-медиатора для новостей.
 * Отвечает за создание [RemoteMediator], который координирует загрузку данных из сети и сохранение их в локальную базу данных.
 *
 * @property remoteRepository Репозиторий для работы с сетевыми данными.
 * @property storageRepository Репозиторий для работы с локальным хранилищем.
 */
class NewsMediatorRepositoryImpl @Inject constructor(
    private val remoteRepository: NewsRemoteRepository,
    private val storageRepository: NewsStorageRepository
) : NewsMediatorRepository {

    /**
     * Создает и возвращает [RemoteMediator] для пагинации новостей конкретного подразделения.
     * Используется библиотекой Paging 3 для подгрузки данных при скролле.
     *
     * @param newsSubdivision ID подразделения новостей (например, университет или деканат).
     * @return Объект [RemoteMediator] для управления пагинацией.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun newsMediator(newsSubdivision: Int): RemoteMediator<Int, NewsPost> {
        // Создаем источник данных, связывающий сеть и БД
        return NewsRemoteSource(
            newsSubdivision = newsSubdivision,
            remoteRepository = remoteRepository,
            storageRepository = storageRepository
        )
    }
}