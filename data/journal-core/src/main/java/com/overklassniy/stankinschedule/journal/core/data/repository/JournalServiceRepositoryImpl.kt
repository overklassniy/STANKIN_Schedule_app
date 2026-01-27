package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.mapper.toSemesterMarks
import com.overklassniy.stankinschedule.journal.core.data.mapper.toStudent
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.inject.Inject


/**
 * Реализация репозитория сервиса журнала [JournalServiceRepository].
 * Отвечает за взаимодействие с сетевым API модульного журнала.
 *
 * @param api Интерфейс API для выполнения запросов к серверу журнала.
 */
class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalServiceRepository {

    /**
     * Загружает список семестров и информацию о студенте.
     *
     * Алгоритм:
     * 1. Выполняет запрос к API [api.getSemesters] с переданными учетными данными.
     * 2. Ожидает ответ сервера.
     * 3. Преобразует ответ (DTO) в доменную модель [Student] с помощью маппера [toStudent].
     *
     * @param credentials Учетные данные студента (логин и пароль).
     * @return Объект [Student], содержащий информацию о студенте и список доступных семестров.
     */
    override suspend fun loadSemesters(
        credentials: StudentCredentials,
    ): Student {
        return api.getSemesters(credentials.login, credentials.password)
            .await()
            .toStudent()
    }

    /**
     * Загружает оценки за конкретный семестр.
     *
     * Алгоритм:
     * 1. Выполняет запрос к API [api.getMarks] с учетными данными и ID семестра.
     * 2. Ожидает ответ сервера.
     * 3. Преобразует ответ (DTO) в доменную модель [SemesterMarks] с помощью маппера [toSemesterMarks].
     *
     * @param credentials Учетные данные студента.
     * @param semester Идентификатор семестра, за который нужно получить оценки.
     * @return Объект [SemesterMarks], содержащий список оценок за выбранный семестр.
     */
    override suspend fun loadMarks(
        credentials: StudentCredentials,
        semester: String,
    ): SemesterMarks {
        return api.getMarks(credentials.login, credentials.password, semester)
            .await()
            .toSemesterMarks()
    }
}