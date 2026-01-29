package com.overklassniy.stankinschedule.schedule.repository.domain.repository

import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem

/**
 * Интерфейс локального хранилища данных репозитория.
 *
 * Отвечает за кэширование описания репозитория и списков расписаний.
 */
interface RepositoryStorage {

    /**
     * Загружает кэшированное описание репозитория.
     *
     * @return Контейнер с описанием репозитория [RepositoryDescription] или null, если кэш пуст.
     */
    suspend fun loadDescription(): CacheContainer<RepositoryDescription>?

    /**
     * Сохраняет описание репозитория в кэш.
     *
     * @param description Описание репозитория для сохранения.
     */
    suspend fun saveDescription(description: RepositoryDescription)

    /**
     * Сохраняет список элементов расписания в базу данных.
     *
     * @param entries Список элементов [RepositoryItem] для вставки.
     */
    suspend fun insertRepositoryEntries(entries: List<RepositoryItem>)

    /**
     * Получает список элементов расписания для указанной категории.
     *
     * @param category Название категории.
     * @return Список элементов расписания [RepositoryItem].
     */
    suspend fun getRepositoryEntries(category: String): List<RepositoryItem>

    /**
     * Очищает все записи репозитория.
     */
    suspend fun clearEntries()
}