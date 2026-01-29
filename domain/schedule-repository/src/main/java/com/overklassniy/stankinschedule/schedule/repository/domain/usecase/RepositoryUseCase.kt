package com.overklassniy.stankinschedule.schedule.repository.domain.usecase


import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import javax.inject.Inject


/**
 * UseCase для работы с репозиторием расписаний.
 *
 * Предоставляет методы для получения описания и элементов расписаний с поддержкой кэширования.
 */
class RepositoryUseCase @Inject constructor(
    private val service: ScheduleRemoteService,
    private val repositoryStorage: RepositoryStorage,
    private val scheduleStorage: ScheduleStorage,
) {

    /**
     * Получает описание репозитория с поддержкой кэширования.
     *
     * Если кэш валиден, возвращает данные из кэша, иначе загружает с сервера и обновляет кэш.
     *
     * @param useCache Использовать ли кэш.
     * @param expireHours Время жизни кэша в часах.
     * @return Поток с описанием репозитория [RepositoryDescription].
     */
    fun repositoryDescription(
        useCache: Boolean = true,
        expireHours: Int = 24
    ): Flow<RepositoryDescription> = flow {
        val cache = repositoryStorage.loadDescription()
        if (cache != null && useCache && (cache.cacheTime subHours DateTime.now() < expireHours)) {
            emit(cache.data)
            return@flow
        }

        try {
            val description = service.description()
            repositoryStorage.saveDescription(description)
            repositoryStorage.clearEntries()
            emit(description)

        } catch (e: Exception) {
            if (cache != null) {
                emit(cache.data)
                return@flow
            }

            throw e
        }
    }

    /**
     * Получает список элементов расписания для категории.
     *
     * @param category Название категории.
     * @param useCache Использовать ли кэш.
     * @return Поток со списком элементов [RepositoryItem].
     */
    fun repositoryItems(
        category: String,
        useCache: Boolean = true,
    ): Flow<List<RepositoryItem>> = flow {
        val cache = repositoryStorage.getRepositoryEntries(category)
        if (cache.isNotEmpty() && useCache) {
            emit(cache)
            return@flow
        }

        val response = service.category(category)
        repositoryStorage.insertRepositoryEntries(response)
        emit(response)
    }

    /**
     * Проверяет, существует ли расписание с таким именем в локальном хранилище.
     *
     * @param scheduleName Название расписания.
     * @return true, если расписание существует, иначе false.
     */
    suspend fun isScheduleExist(scheduleName: String): Boolean {
        return scheduleStorage.isScheduleExist(scheduleName)
    }
}