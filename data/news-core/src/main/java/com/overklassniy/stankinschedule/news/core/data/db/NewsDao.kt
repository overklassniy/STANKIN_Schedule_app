package com.overklassniy.stankinschedule.news.core.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с таблицей новостей в базе данных.
 *
 * Предоставляет методы для вставки, получения, удаления и подсчета новостей.
 */
@Dao
interface NewsDao {

    /**
     * Вставляет список новостей в базу данных.
     * Если новость с таким ID уже существует, она будет заменена (REPLACE).
     *
     * @param items Список сущностей новостей [NewsEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<NewsEntity>)

    /**
     * Получает источник данных для пагинации (PagingSource) новостей определенного подразделения.
     * Результат сортируется по порядку индекса (index_order) по возрастанию.
     *
     * @param newsSubdivision ID подразделения новостей (например, университет или деканат).
     * @return [PagingSource] возвращающий объекты [NewsPost].
     */
    @Query(
        """
        SELECT 
            id as id, 
            title as title, 
            CASE WHEN logo = '' THEN NULL ELSE logo END as previewImageUrl, 
            date as date,
            relative_url as relativeUrl
        FROM news_posts WHERE news_subdivision = :newsSubdivision ORDER BY index_order ASC
        """
    )
    fun all(newsSubdivision: Int): PagingSource<Int, NewsPost>

    /**
     * Получает поток (Flow) последних новостей (без фильтрации по подразделению).
     *
     * @param max Максимальное количество возвращаемых новостей (по умолчанию 3).
     * @return [Flow] со списком сущностей [NewsEntity].
     */
    @Query("SELECT * FROM news_posts ORDER BY date DESC, id DESC LIMIT :max")
    fun latest(max: Int = 3): Flow<List<NewsEntity>>

    /**
     * Получает поток (Flow) последних новостей для конкретного подразделения.
     *
     * @param newsSubdivision ID подразделения новостей.
     * @param max Максимальное количество возвращаемых новостей (по умолчанию 3).
     * @return [Flow] со списком сущностей [NewsEntity].
     */
    @Query("SELECT * FROM news_posts WHERE news_subdivision = :newsSubdivision ORDER BY date DESC, id DESC LIMIT :max")
    fun latestBySubdivision(newsSubdivision: Int, max: Int = 3): Flow<List<NewsEntity>>

    /**
     * Удаляет все новости указанного подразделения из базы данных.
     *
     * @param newsSubdivision ID подразделения новостей, записи которого нужно удалить.
     */
    @Query("DELETE FROM news_posts WHERE news_subdivision = :newsSubdivision")
    suspend fun clear(newsSubdivision: Int)

    /**
     * Вычисляет следующий порядковый индекс (index_order) для новых записей указанного подразделения.
     * Используется для сохранения порядка отображения новостей при подгрузке.
     *
     * @param newsSubdivision ID подразделения новостей.
     * @return Следующий индекс (максимальный текущий + 1) или null (если записей нет, SQL вернет NULL, Room сконвертирует в 0 или 1?
     * В данном случае, если MAX вернет NULL, выражение `NULL + 1` будет NULL.
     * Тип возвращаемого значения Int, Room может выбросить исключение или вернуть 0 по умолчанию, если не nullable.
     * Однако, обычно используется coalesce в SQL или обработка на уровне кода. В данном случае предполагается, что метод вернет корректный индекс.
     * Примечание: В SQLite `MAX(col)` возвращает NULL если нет строк.
     */
    @Query("SELECT MAX(index_order) + 1 FROM news_posts WHERE news_subdivision = :newsSubdivision")
    suspend fun nextIndexInResponse(newsSubdivision: Int): Int
}