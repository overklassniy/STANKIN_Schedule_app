package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthToken
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalSemester
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student


/**
 * Репозиторий для взаимодействия с удаленным сервисом (API) журнала.
 *
 * Выполняет сетевые запросы для получения данных.
 */
interface JournalServiceRepository {

    /**
     * Загружает данные о студенте и список семестров.
     *
     * @param token Токен доступа к API личного кабинета.
     * @return Объект [Student].
     */
    suspend fun loadStudentInfo(token: JournalAuthToken): Student

    /**
     * Загружает оценки за конкретный семестр.
     *
     * @param token Токен доступа к API личного кабинета.
     * @param semester Семестр, за который нужно получить оценки.
     * @return Объект [SemesterMarks].
     */
    suspend fun loadMarks(token: JournalAuthToken, semester: JournalSemester): SemesterMarks
}
