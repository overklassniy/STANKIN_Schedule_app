package com.overklassniy.stankinschedule.journal.core.domain.usecase


import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * UseCase для авторизации студента в системе журнала.
 *
 * Отвечает за вход (с валидацией через загрузку данных) и выход из системы.
 */
class LoginUseCase @Inject constructor(
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
) {

    /**
     * Выполняет вход в систему.
     *
     * 1. Создает объект учетных данных.
     * 2. Пытается загрузить данные студента (семестры) для проверки валидности логина/пароля.
     * 3. В случае успеха сохраняет учетные данные в безопасное хранилище и данные студента в кэш.
     *
     * @param login Логин студента.
     * @param password Пароль студента.
     * @return [Flow] с загруженным объектом [Student].
     * @throws Exception Если логин/пароль неверны или произошла ошибка сети (пробрасывается из репозитория).
     */
    fun signIn(login: String, password: String): Flow<Student> = flow {
        val possibleCredentials = StudentCredentials(login, password)

        // Если не верны, то тут будет исключение с Http 401
        val student = service.loadSemesters(possibleCredentials)

        secure.signIn(possibleCredentials)
        storage.saveStudent(student)

        emit(student)
    }.flowOn(Dispatchers.IO)

    /**
     * Выполняет выход из системы.
     *
     * Очищает локальный кэш и удаляет сохраненные учетные данные.
     */
    suspend fun signOut() {
        storage.clear()
        secure.signOut()
    }
}