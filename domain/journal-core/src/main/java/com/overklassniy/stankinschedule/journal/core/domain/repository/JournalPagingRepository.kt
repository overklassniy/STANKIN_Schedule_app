package com.overklassniy.stankinschedule.journal.core.domain.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks

/**
 * Интерфейс репозитория для создания источника данных постраничной загрузки (Paging).
 *
 * Используется для отображения списка семестров и их оценок в виде ленты.
 */
interface JournalPagingRepository {

    /**
     * Создает источник данных PagingSource для загрузки оценок по семестрам.
     *
     * @param journal Репозиторий журнала для загрузки данных.
     * @param semesters Список названий семестров, которые нужно загрузить.
     * @param semesterExpireHours Время жизни кэша семестра в часах.
     * @return Объект [PagingSource], предоставляющий страницы данных типа [SemesterMarks] с ключами типа [String] (названия семестров).
     */
    fun semesterSource(
        journal: JournalRepository,
        semesters: List<String>,
        semesterExpireHours: Int
    ): PagingSource<String, SemesterMarks>
}