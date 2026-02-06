package com.overklassniy.stankinschedule.news.core.domain.usecase

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.core.domain.ext.subMinutes
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * UseCase для просмотра ленты новостей и управления их загрузкой.
 *
 * Отвечает за получение списка новостей (с пагинацией и без), а также за их обновление
 * с учетом кэширования и настроек.
 */
class NewsReviewUseCase @Inject constructor(
    @ApplicationContext context: Context,
    private val storageRepository: NewsStorageRepository,
    private val remoteRepository: NewsRemoteRepository,
    private val preferenceRepository: NewsPreferenceRepository,
) {
    // Проверка, запущено ли приложение в режиме отладки
    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Создает источник данных для постраничной загрузки новостей.
     * Используется библиотекой Paging 3 для отображения списка.
     *
     * @param subdivision Идентификатор подразделения новостей.
     * @return [PagingSource] для загрузки [NewsPost].
     */
    fun news(subdivision: Int): PagingSource<Int, NewsPost> =
        storageRepository.news(subdivision)

    /**
     * Получает поток с последними новостями указанного подразделения.
     *
     * @param newsSubdivision Идентификатор подразделения новостей.
     * @param newsCount Количество новостей для загрузки.
     * @return [Flow] со списком последних [NewsPost].
     */
    fun lastNews(newsSubdivision: Int, newsCount: Int): Flow<List<NewsPost>> =
        storageRepository.lastNews(newsSubdivision, newsCount)

    /**
     * Обновляет новости для указанного подразделения.
     *
     * Загружает первую страницу новостей из сети и сохраняет её в базу данных.
     * Обновление происходит только если:
     * - Приложение в режиме отладки ([isDebug]).
     * - Передан флаг принудительного обновления ([force]).
     * - Новости еще не обновлялись (`lastRefresh` == null).
     * - Прошло более 30 минут с последнего обновления.
     *
     * @param subdivision Идентификатор подразделения новостей.
     * @param force Флаг принудительного обновления (по умолчанию false).
     */
    suspend fun refreshNews(subdivision: Int, force: Boolean = false) {
        val lastRefresh = preferenceRepository.currentNewsDateTime(subdivision)

        // Проверяем условия обновления: отладка, принудительно, нет данных или устарели (>30 мин)
        if (isDebug || force || lastRefresh == null || lastRefresh subMinutes DateTime.now() > 30) {
            if (force) remoteRepository.invalidateCache()
            // Загружаем первую страницу из сети
            val posts = remoteRepository.loadPage(subdivision, page = 1)
            // Сохраняем в локальную БД с заменой старых данных (isRefresh = true)
            storageRepository.saveNews(subdivision, posts, true)
            // Обновляем время последнего обновления
            preferenceRepository.updateNewsDateTime(subdivision)
        }
    }
}