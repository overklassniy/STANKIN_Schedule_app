package com.overklassniy.stankinschedule.news.core.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost

/**
 * Репозиторий для получения RemoteMediator, используемого в Paging 3.
 *
 * RemoteMediator управляет загрузкой данных из сети и сохранением их в локальную базу данных
 * при использовании библиотеки Paging.
 */
interface NewsMediatorRepository {

    /**
     * Создает экземпляр RemoteMediator для указанного подразделения новостей.
     *
     * @param newsSubdivision Идентификатор подразделения.
     * @return [RemoteMediator] для пагинации [NewsPost].
     */
    @OptIn(ExperimentalPagingApi::class)
    fun newsMediator(newsSubdivision: Int): RemoteMediator<Int, NewsPost>
}