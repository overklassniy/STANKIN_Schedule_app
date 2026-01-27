package com.overklassniy.stankinschedule.news.core.data.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.overklassniy.stankinschedule.news.core.data.db.NewsDao
import com.overklassniy.stankinschedule.news.core.data.db.NewsDatabase
import com.overklassniy.stankinschedule.news.core.data.mapper.toEntity
import com.overklassniy.stankinschedule.news.core.data.mapper.toPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

/**
 * Реализация репозитория для работы с локальным хранилищем новостей (Room Database).
 * Отвечает за сохранение, чтение и очистку новостей в базе данных.
 *
 * @property dbProvider Провайдер для доступа к экземпляру базы данных [NewsDatabase].
 * @property daoProvider Провайдер для доступа к DAO новостей [NewsDao].
 */
class NewsStorageRepositoryImpl @Inject constructor(
    private val dbProvider: Provider<NewsDatabase>,
    private val daoProvider: Provider<NewsDao>,
) : NewsStorageRepository {

    // Ленивое получение экземпляра БД
    private val db: NewsDatabase
        get() = dbProvider.get()

    // Ленивое получение DAO
    private val dao: NewsDao
        get() = daoProvider.get()

    /**
     * Возвращает источник данных для Paging 3, который читает новости из БД.
     * Автоматически маппит сущности БД в доменные модели [NewsPost].
     *
     * @param newsSubdivision ID подразделения новостей.
     * @return [PagingSource] для постраничной загрузки.
     */
    override fun news(newsSubdivision: Int): PagingSource<Int, NewsPost> {
        // Room автоматически генерирует PagingSource, который поддерживает маппинг
        return dao.all(newsSubdivision) // .transform(mapper = { it.toPost() })
    }

    /**
     * Возвращает поток последних N новостей из всех подразделений.
     * Используется, например, для виджета на главном экране.
     *
     * @param newsCount Количество новостей для загрузки.
     * @return [Flow] со списком новостей.
     */
    override fun lastNews(newsCount: Int): Flow<List<NewsPost>> {
        return dao.latest(newsCount).map { last -> last.map { it.toPost() } }
    }

    /**
     * Возвращает поток последних N новостей конкретного подразделения.
     *
     * @param newsSubdivision ID подразделения новостей.
     * @param newsCount Количество новостей для загрузки.
     * @return [Flow] со списком новостей.
     */
    override fun lastNews(newsSubdivision: Int, newsCount: Int): Flow<List<NewsPost>> {
        return dao.latestBySubdivision(newsSubdivision, newsCount)
            .map { last -> last.map { it.toPost() } }
    }

    /**
     * Сохраняет список новостей в базу данных.
     * Выполняется в транзакции.
     *
     * @param newsSubdivision ID подразделения новостей.
     * @param posts Список новостей для сохранения.
     * @param force Если true, старые новости данного подразделения будут удалены перед вставкой.
     */
    override suspend fun saveNews(newsSubdivision: Int, posts: List<NewsPost>, force: Boolean) {
        db.withTransaction {
            // Если требуется принудительное обновление, очищаем кэш для этого подразделения
            if (force) {
                dao.clear(newsSubdivision)
            }

            // Вычисляем начальный индекс для сохранения порядка сортировки
            val start = dao.nextIndexInResponse(newsSubdivision)

            // Преобразуем доменные модели в сущности БД с простановкой индексов
            val news = posts.mapIndexed { index, news ->
                news.toEntity(start + index, newsSubdivision)
            }

            dao.insert(news)
        }
    }

    /**
     * Удаляет все новости указанного подразделения из базы данных.
     *
     * @param newsSubdivision ID подразделения новостей.
     */
    override suspend fun clearNews(newsSubdivision: Int) {
        db.withTransaction {
            dao.clear(newsSubdivision)
        }
    }
}