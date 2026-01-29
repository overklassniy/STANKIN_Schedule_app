package com.overklassniy.stankinschedule.schedule.repository.domain.repository

import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem


/**
 * Удаленный сервис для получения информации о репозитории расписаний.
 */
interface ScheduleRemoteService {

    /**
     * Получает описание репозитория (метаданные).
     *
     * @return Описание репозитория [RepositoryDescription].
     */
    suspend fun description(): RepositoryDescription

    /**
     * Получает список элементов расписания для заданной категории.
     *
     * @param category Название категории.
     * @return Список элементов расписания [RepositoryItem].
     */
    suspend fun category(category: String): List<RepositoryItem>
}