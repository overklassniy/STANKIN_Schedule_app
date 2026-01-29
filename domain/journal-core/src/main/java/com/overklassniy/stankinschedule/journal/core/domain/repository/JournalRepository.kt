package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student


/**
 * Основной репозиторий для взаимодействия с данными журнала.
 *
 * Объединяет логику работы с локальным кэшем и удаленным сервисом.
 */
interface JournalRepository {

    /**
     * Получает информацию о студенте (ФИО, группа, список семестров).
     *
     * @param useCache Использовать ли кэшированные данные (по умолчанию `true`). Если `false` или кэш пуст/устарел, данные будут загружены из сети.
     * @return Объект [Student].
     */
    suspend fun student(useCache: Boolean = true): Student

    /**
     * Получает оценки за указанный семестр.
     *
     * @param semester Название семестра.
     * @param semesterExpireHours Время жизни кэша в часах (по умолчанию 2 часа).
     * @param useCache Использовать ли кэш (по умолчанию `true`).
     * @return Объект [SemesterMarks] с оценками.
     */
    suspend fun semesterMarks(
        semester: String,
        semesterExpireHours: Int = 2,
        useCache: Boolean = true,
    ): SemesterMarks
}