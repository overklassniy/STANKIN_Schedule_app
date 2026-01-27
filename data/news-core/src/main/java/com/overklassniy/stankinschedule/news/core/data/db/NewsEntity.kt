package com.overklassniy.stankinschedule.news.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность базы данных, представляющая новостную запись.
 * Хранится в таблице "news_posts".
 *
 * @property id Уникальный идентификатор записи в БД (автогенерируемый).
 * @property indexOrder Порядковый индекс новости для сортировки и пагинации.
 * @property newsSubdivision Идентификатор подразделения новостей (например, университет или деканат).
 * @property title Заголовок новости.
 * @property date Дата публикации новости (в строковом формате).
 * @property logo URL или путь к изображению превью (логотипу) новости.
 * @property relativeUrl Относительный URL новости. Используется как уникальный ключ для предотвращения дубликатов.
 */
@Entity(
    tableName = "news_posts",
    indices = [
        // Индекс для обеспечения уникальности новостей по их URL
        Index("relative_url", unique = true),
        // Составной индекс для оптимизации запросов по подразделению и порядку сортировки
        Index(
            value = ["news_subdivision", "index_order"],
            unique = false,
        ),
    ]
)
data class NewsEntity(
    /**
     * Первичный ключ. Генерируется автоматически.
     */
    @PrimaryKey(autoGenerate = true) val id: Int,

    /**
     * Порядковый номер новости в списке.
     */
    @ColumnInfo(name = "index_order") val indexOrder: Int,

    /**
     * ID подразделения (источника) новостей.
     */
    @ColumnInfo(name = "news_subdivision") val newsSubdivision: Int,

    /**
     * Заголовок новости.
     */
    @ColumnInfo(name = "title") val title: String,

    /**
     * Дата новости.
     */
    @ColumnInfo(name = "date") val date: String,

    /**
     * Ссылка на изображение превью.
     */
    @ColumnInfo(name = "logo") val logo: String,

    /**
     * Относительная ссылка на новость. Может быть null.
     */
    @ColumnInfo(name = "relative_url") val relativeUrl: String?,
)