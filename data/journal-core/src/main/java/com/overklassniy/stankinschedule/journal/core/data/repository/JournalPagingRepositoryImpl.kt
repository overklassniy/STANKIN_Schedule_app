package com.overklassniy.stankinschedule.journal.core.data.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.journal.core.data.source.SemesterMarksSource
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPagingRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * Реализация репозитория для пагинации данных журнала.
 * Отвечает за создание источников данных (PagingSource) для списков оценок.
 */
class JournalPagingRepositoryImpl @Inject constructor() : JournalPagingRepository {

    /**
     * Создает источник данных (PagingSource) для загрузки оценок по семестрам.
     * Используется для отображения списка семестров с оценками с поддержкой подгрузки.
     *
     * @param journal Репозиторий журнала для получения данных [JournalRepository]
     * @param semesters Список идентификаторов семестров, для которых нужно загрузить оценки
     * @param semesterExpireHours Время жизни кэша семестра в часах (для определения необходимости обновления)
     * @return Источник данных [PagingSource] для постраничной загрузки оценок [SemesterMarks]
     */
    override fun semesterSource(
        journal: JournalRepository,
        semesters: List<String>,
        semesterExpireHours: Int
    ): PagingSource<String, SemesterMarks> {
        return SemesterMarksSource(
            journal = journal,
            semesters = semesters,
            semesterExpireHours = semesterExpireHours
        )
    }
}