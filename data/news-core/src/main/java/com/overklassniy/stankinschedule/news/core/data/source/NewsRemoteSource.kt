package com.overklassniy.stankinschedule.news.core.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository

/**
 * Реализация RemoteMediator для Paging 3.
 * Отвечает за координацию загрузки данных из сети и кэширования их в локальной базе данных.
 * Позволяет реализовать паттерн "Network + Database" для офлайн-доступа и пагинации.
 *
 * @property newsSubdivision ID подразделения новостей, для которого работает этот источник.
 * @property remoteRepository Репозиторий для загрузки данных из сети.
 * @property storageRepository Репозиторий для сохранения данных в локальную БД.
 */
@OptIn(ExperimentalPagingApi::class)
class NewsRemoteSource(
    private val newsSubdivision: Int,
    private val remoteRepository: NewsRemoteRepository,
    private val storageRepository: NewsStorageRepository
) : RemoteMediator<Int, NewsPost>() {

    /**
     * Определяет необходимость начального обновления данных при запуске.
     * Возвращает [RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH], чтобы не перегружать сеть
     * и отображать кэшированные данные, если они есть. Обновление произойдет при явном запросе
     * или истечении срока жизни кэша (если реализовано).
     *
     * @return Действие инициализации.
     */
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    /**
     * Загружает данные в зависимости от типа загрузки (Refresh, Append, Prepend).
     *
     * Алгоритм:
     * 1. Определяет номер страницы на основе [loadType].
     * 2. Загружает данные из [remoteRepository].
     * 3. Сохраняет данные в [storageRepository] (с очисткой при REFRESH).
     * 4. Возвращает результат (успех или конец списка).
     *
     * @param loadType Тип загрузки (обновление, добавление в начало, добавление в конец).
     * @param state Текущее состояние пагинации.
     * @return Результат работы медиатора [RemoteMediator.MediatorResult].
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsPost>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true) // Загрузка предыдущих страниц не поддерживается
                LoadType.APPEND -> state.pages.size + 1 // Простая логика: следующая страница = кол-во загруженных + 1
            }

            // Загружаем новости из сети
            val response = remoteRepository.loadPage(newsSubdivision, page, state.config.pageSize)

            // Сохраняем в БД (если REFRESH, то старые данные удаляются)
            storageRepository.saveNews(newsSubdivision, response, loadType == LoadType.REFRESH)

            // Если список пуст, значит достигнут конец пагинации
            MediatorResult.Success(response.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}