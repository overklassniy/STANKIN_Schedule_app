package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.mapper.toSemesterMarks
import com.overklassniy.stankinschedule.journal.core.data.mapper.toStudent
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.net.ssl.HttpsURLConnection
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
     * 1. Выполняет запрос к API `api.getSemesters` с переданными учетными данными.
     * 2. Ожидает ответ сервера.
     * 3. При HTTP 401 выбрасывает [StudentAuthorizedException] — сессия истекла.
     * 4. Преобразует ответ (DTO) в доменную модель [Student] с помощью маппера [toStudent].
     *
     * @param credentials Учетные данные студента (логин и пароль).
     * @return Объект [Student], содержащий информацию о студенте и список доступных семестров.
     * @throws StudentAuthorizedException если сервер вернул HTTP 401 (сессия истекла).
     */
    override suspend fun loadSemesters(
        credentials: StudentCredentials,
    ): Student {
        return try {
            api.getSemesters(credentials.login, credentials.password)
                .await()
                .toStudent()
        } catch (e: retrofit2.HttpException) {
            if (e.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                throw StudentAuthorizedException(e)
            }
            throw e
        }
    }

    /**
     * Загружает оценки за конкретный семестр.
     *
     * Алгоритм:
     * 1. Выполняет запрос к API `api.getMarks` с учетными данными и ID семестра.
     * 2. Ожидает ответ сервера.
     * 3. При HTTP 401 выбрасывает [StudentAuthorizedException] — сессия истекла.
     * 4. Преобразует ответ (DTO) в доменную модель [SemesterMarks] с помощью маппера [toSemesterMarks].
     *
     * @param credentials Учетные данные студента.
     * @param semester Идентификатор семестра, за который нужно получить оценки.
     * @return Объект [SemesterMarks], содержащий список оценок за выбранный семестр.
     * @throws StudentAuthorizedException если сервер вернул HTTP 401 (сессия истекла).
     */
    override suspend fun loadMarks(
        credentials: StudentCredentials,
        semester: String,
    ): SemesterMarks {
        return try {
            api.getMarks(credentials.login, credentials.password, semester)
                .await()
                .toSemesterMarks()
        } catch (e: retrofit2.HttpException) {
            if (e.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                throw StudentAuthorizedException(e)
            }
            throw e
        }
    }
}