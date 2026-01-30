package com.overklassniy.stankinschedule.schedule.repository.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность базы данных Room, представляющая запись в репозитории расписаний.
 *
 * Хранит информацию о названии, пути к файлу и категории (группа, преподаватель и т.д.).
 *
 * @property name Название записи (например, номер группы или имя преподавателя).
 * @property path Относительный путь к файлу расписания.
 * @property category Категория записи (определяет тип расписания).
 */
@Entity(
    tableName = "repository_entries",
    indices = [
        Index("category"),
    ]
)
data class RepositoryEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "category") val category: String,
) {

    /**
     * Уникальный идентификатор записи в базе данных.
     *
     * Генерируется автоматически. Используется Room для управления записями.
     * Подавление предупреждения "unused", так как поле используется фреймворком.
     */
    @PrimaryKey(autoGenerate = true)
    @Suppress("Unused")
    var id: Long = 0
}