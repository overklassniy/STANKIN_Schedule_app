package com.overklassniy.stankinschedule.news.core.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для работы с БД новостей.
 */
@Dao
interface NewsDao {

    /**
     * Вставка (обновление) новостей в БД.
     * @param items список новостей.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<NewsEntity>)

    /**
     * Возвращает список (DataSource) за кэшированных новостей.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query(
        """
        SELECT 
            id as id, 
            title as title, 
            logo as previewImageUrl, 
            date as date 
        FROM news_posts WHERE news_subdivision = :newsSubdivision ORDER BY index_order ASC
        """
    )
    fun all(newsSubdivision: Int): PagingSource<Int, NewsPost>

    /**
     * Возвращает список (DataSource) из последних нескольких элементов.
     * @param max максимальное количество элементов.
     */
    @Query("SELECT * FROM news_posts ORDER BY date DESC, id DESC LIMIT :max")
    fun latest(max: Int = 3): Flow<List<NewsEntity>>

    /**
     * Возвращает список из последних нескольких элементов для конкретного раздела.
     * @param newsSubdivision номер отдела новостей.
     * @param max максимальное количество элементов.
     */
    @Query("SELECT * FROM news_posts WHERE news_subdivision = :newsSubdivision ORDER BY date DESC, id DESC LIMIT :max")
    fun latestBySubdivision(newsSubdivision: Int, max: Int = 3): Flow<List<NewsEntity>>

    /**
     * Очищает закэшированные новости.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("DELETE FROM news_posts WHERE news_subdivision = :newsSubdivision")
    suspend fun clear(newsSubdivision: Int)

    /**
     * Количество новостей в кэше.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("SELECT COUNT(*) FROM news_posts WHERE news_subdivision = :newsSubdivision")
    suspend fun count(newsSubdivision: Int): Int

    /**
     * Возвращает следующий порядковый индекс для новостей.
     * @param newsSubdivision номер отдела новостей.
     */
    @Query("SELECT MAX(index_order) + 1 FROM news_posts WHERE news_subdivision = :newsSubdivision")
    suspend fun nextIndexInResponse(newsSubdivision: Int): Int
}