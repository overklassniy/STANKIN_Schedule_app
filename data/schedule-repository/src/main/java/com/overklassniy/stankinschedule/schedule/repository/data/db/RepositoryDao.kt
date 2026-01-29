package com.overklassniy.stankinschedule.schedule.repository.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) для работы с таблицей репозитория расписаний.
 *
 * Предоставляет методы для сохранения, получения и удаления записей о расписаниях
 * (группы, преподаватели, аудитории) в локальной базе данных Room.
 */
@Dao
interface RepositoryDao {

    /**
     * Вставляет список сущностей репозитория в базу данных.
     *
     * Если запись с таким же первичным ключом уже существует, она будет перезаписана.
     *
     * @param entities Список объектов [RepositoryEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<RepositoryEntity>)

    /**
     * Получает список всех записей репозитория для указанной категории.
     *
     * Результат сортируется по имени записи в алфавитном порядке.
     *
     * @param category Категория расписания (например, группы или преподаватели).
     * @return Список объектов [RepositoryEntity], соответствующих заданной категории.
     */
    @Query("SELECT * FROM repository_entries WHERE category = :category ORDER BY name")
    suspend fun getAll(category: String): List<RepositoryEntity>

    /**
     * Удаляет все записи из таблицы репозитория.
     *
     * Используется для полной очистки кэшированных данных перед загрузкой новых.
     */
    @Query("DELETE FROM repository_entries")
    suspend fun deleteAll()
}