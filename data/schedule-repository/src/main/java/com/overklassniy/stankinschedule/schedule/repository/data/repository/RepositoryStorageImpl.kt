package com.overklassniy.stankinschedule.schedule.repository.data.repository

import androidx.room.withTransaction
import com.overklassniy.stankinschedule.core.data.cache.CacheManager
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryDatabase
import com.overklassniy.stankinschedule.schedule.repository.data.mapper.toEntity
import com.overklassniy.stankinschedule.schedule.repository.data.mapper.toItem
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import javax.inject.Inject
import javax.inject.Provider

/**
 * Реализация хранилища данных репозитория расписаний.
 *
 * Использует [CacheManager] для работы с метаданными и [RepositoryDao] для работы с элементами репозитория в БД.
 *
 * @property cache Менеджер кэша для сохранения описания репозитория.
 * @property dbProvider Провайдер базы данных Room.
 * @property daoProvider Провайдер DAO для доступа к данным репозитория.
 */
class RepositoryStorageImpl @Inject constructor(
    private val cache: CacheManager,
    private val dbProvider: Provider<RepositoryDatabase>,
    private val daoProvider: Provider<RepositoryDao>,
) : RepositoryStorage {

    private val db: RepositoryDatabase
        get() = dbProvider.get()

    private val dao: RepositoryDao
        get() = daoProvider.get()

    init {
        // Регистрируем корневой путь в кэше
        cache.addStartedPath(ROOT)
    }

    /**
     * Загружает описание репозитория из кэша.
     *
     * @return Контейнер [CacheContainer] с описанием [RepositoryDescription], или null, если не найдено.
     */
    override suspend fun loadDescription(): CacheContainer<RepositoryDescription>? {
        return cache.loadFromCache(RepositoryDescription::class.java, DESCRIPTION)
    }

    /**
     * Сохраняет описание репозитория в кэш.
     *
     * @param description Описание репозитория [RepositoryDescription] для сохранения.
     */
    override suspend fun saveDescription(description: RepositoryDescription) {
        cache.saveToCache(description, DESCRIPTION)
    }

    /**
     * Вставляет список элементов репозитория в базу данных.
     *
     * Операция выполняется в транзакции: сначала удаляются все старые записи, затем вставляются новые.
     *
     * @param entries Список элементов [RepositoryItem] для сохранения.
     */
    override suspend fun insertRepositoryEntries(entries: List<RepositoryItem>) {
        db.withTransaction {
            dao.deleteAll()
            dao.insertAll(entries.map { it.toEntity() })
        }
    }

    /**
     * Получает список элементов репозитория по указанной категории.
     *
     * @param category Категория элементов (например, группы или преподаватели).
     * @return Список элементов [RepositoryItem], соответствующих категории.
     */
    override suspend fun getRepositoryEntries(category: String): List<RepositoryItem> {
        return dao.getAll(category).map { it.toItem() }
    }

    /**
     * Удаляет все записи из таблицы репозитория.
     *
     * Используется для полной очистки данных.
     */
    override suspend fun clearEntries() {
        dao.deleteAll()
    }

    companion object {
        private const val ROOT = "firebase_storage"
        private const val DESCRIPTION = "description"
    }
}