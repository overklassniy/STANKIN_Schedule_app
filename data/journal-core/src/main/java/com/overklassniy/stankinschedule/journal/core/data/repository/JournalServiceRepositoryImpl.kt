package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.mapper.toSemesterMarks
import com.overklassniy.stankinschedule.journal.core.data.mapper.toSemesters
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthToken
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalSemester
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.HttpException
import retrofit2.await
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


/**
 * Реализация репозитория сервиса журнала [JournalServiceRepository].
 * Отвечает за взаимодействие с сетевым API личного кабинета.
 *
 * @param api Интерфейс API для выполнения запросов к серверу.
 */
class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalServiceRepository {

    /**
     * Загружает информацию о студенте и список доступных семестров.
     *
     * Алгоритм:
     * 1. Запрашивает успеваемость без параметров — ответ содержит
     *    ФИО студента и список `allowedSemesters`.
     * 2. Запрашивает учебную группу (best-effort: при ошибке группа пустая).
     * 3. При HTTP 401 выбрасывает [StudentAuthorizedException] — токен истек.
     *
     * @param token Токен доступа к API.
     * @return Объект [Student] с ФИО, группой и списком семестров.
     * @throws StudentAuthorizedException если сервер вернул HTTP 401 (токен истек).
     */
    override suspend fun loadStudentInfo(token: JournalAuthToken): Student {
        return try {
            val bearer = bearer(token)
            val progress = api.getStudyProgress(bearer).await()

            Student(
                name = progress.data?.fio.orEmpty(),
                group = loadGroup(bearer),
                semesters = progress.toSemesters(),
            )
        } catch (e: HttpException) {
            if (e.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                throw StudentAuthorizedException(e)
            }
            throw e
        }
    }

    /**
     * Загружает название учебной группы (best-effort).
     *
     * @param bearer Заголовок авторизации вида "Bearer <token>".
     * @return Название группы или пустая строка при ошибке запроса.
     * @throws HttpException при HTTP 401 — обрабатывается вызывающим кодом как потеря авторизации.
     */
    private suspend fun loadGroup(bearer: String): String {
        return try {
            api.getEduGroup(bearer).await().eduGroup?.groupName.orEmpty()
        } catch (e: HttpException) {
            if (e.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) throw e
            ""
        }
    }

    /**
     * Загружает оценки за конкретный семестр.
     *
     * Алгоритм:
     * 1. Запрашивает успеваемость по паре (курс, семестр).
     * 2. При HTTP 401 выбрасывает [StudentAuthorizedException] — токен истек.
     * 3. Преобразует период ответа в [SemesterMarks].
     *
     * @param token Токен доступа к API.
     * @param semester Семестр, за который нужны оценки.
     * @return Объект [SemesterMarks] с оценками за семестр.
     * @throws StudentAuthorizedException если сервер вернул HTTP 401 (токен истек).
     */
    override suspend fun loadMarks(
        token: JournalAuthToken,
        semester: JournalSemester,
    ): SemesterMarks {
        return try {
            api.getStudyProgress(
                bearer = bearer(token),
                course = semester.course,
                semester = semester.semester,
            ).await()
                .data?.period.toSemesterMarks()
        } catch (e: HttpException) {
            if (e.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                throw StudentAuthorizedException(e)
            }
            throw e
        }
    }

    /**
     * Формирует значение заголовка Authorization из токена.
     */
    private fun bearer(token: JournalAuthToken): String = "Bearer ${token.accessToken}"
}
