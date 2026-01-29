package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials


/**
 * Репозиторий для взаимодействия с удаленным сервисом (API) журнала.
 *
 * Выполняет сетевые запросы для получения данных.
 */
interface JournalServiceRepository {

    /**
     * Загружает данные о студенте и список семестров.
     *
     * @param credentials Учетные данные для авторизации.
     * @return Объект [Student].
     */
    suspend fun loadSemesters(credentials: StudentCredentials): Student

    /**
     * Загружает оценки за конкретный семестр.
     *
     * @param credentials Учетные данные для авторизации.
     * @param semester Название семестра.
     * @return Объект [SemesterMarks].
     */
    suspend fun loadMarks(credentials: StudentCredentials, semester: String): SemesterMarks
}