package com.overklassniy.stankinschedule.journal.core.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository

/**
 * Источник данных для пагинации оценок по семестрам [PagingSource].
 * Позволяет загружать оценки семестр за семестром, поддерживая навигацию "предыдущий/следующий".
 *
 * @param journal Репозиторий журнала для получения данных об оценках.
 * @param semesters Список идентификаторов всех доступных семестров (в порядке отображения).
 * @param semesterExpireHours Время жизни кэша оценок в часах.
 */
class SemesterMarksSource(
    private val journal: JournalRepository,
    private val semesters: List<String>,
    private val semesterExpireHours: Int,
) : PagingSource<String, SemesterMarks>() {

    /**
     * Загружает данные для запрошенной страницы (семестра).
     *
     * Алгоритм:
     * 1. Определяет текущий семестр (ключ). Если ключ не передан, берет первый семестр из списка.
     * 2. Находит индекс текущего семестра в списке [semesters].
     * 3. Загружает оценки через репозиторий [journal].
     * 4. Формирует результат загрузки [LoadResult.Page], вычисляя ключи для предыдущего и следующего семестров.
     *
     * @param params Параметры загрузки, содержащие ключ (идентификатор семестра).
     * @return [LoadResult] с данными оценок, ключами навигации и информацией о позиции.
     */
    override suspend fun load(
        params: LoadParams<String>,
    ): LoadResult<String, SemesterMarks> {
        return try {
            // Определение текущего семестра (по умолчанию - первый)
            val semester = params.key ?: semesters.first()
            val index = semesters.indexOf(semester)

            // Загрузка оценок из репозитория
            val marks = journal.semesterMarks(semester, semesterExpireHours)

            // Формирование страницы результата с ключами навигации
            LoadResult.Page(
                data = listOf(marks),
                prevKey = semesters.getOrNull(index - 1),
                nextKey = semesters.getOrNull(index + 1),
                itemsBefore = index, // Количество элементов до текущего (для плейсхолдеров)
                itemsAfter = (semesters.size - 1) - index // Количество элементов после текущего
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    /**
     * Определяет ключ для обновления данных (например, при свайпе to refresh).
     *
     * Алгоритм:
     * 1. Получает позицию якоря (anchorPosition) из текущего состояния.
     * 2. Возвращает идентификатор семестра, соответствующий этой позиции.
     *
     * @param state Текущее состояние пагинации.
     * @return Ключ (семестр), который нужно перезагрузить, или null.
     */
    override fun getRefreshKey(state: PagingState<String, SemesterMarks>): String? {
        return state.anchorPosition?.let { anchorPosition -> semesters[anchorPosition] }
    }
}