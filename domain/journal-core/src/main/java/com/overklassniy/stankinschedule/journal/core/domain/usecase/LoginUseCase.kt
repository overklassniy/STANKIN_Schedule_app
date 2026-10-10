package com.overklassniy.stankinschedule.journal.core.domain.usecase


import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthRequest
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalAuthRepository
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
 * Реализует OIDC-поток Authorization Code + PKCE через WebView:
 * создание запроса авторизации, обмен кода на токен и выход из системы.
 */
class LoginUseCase @Inject constructor(
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
    private val auth: JournalAuthRepository,
) {

    /**
     * Подготавливает интерактивный запрос авторизации (с формой логина).
     *
     * @return Запрос с URL для открытия в WebView и state.
     */
    fun beginSignIn(): JournalAuthRequest = auth.createAuthRequest(silent = false)

    /**
     * Подготавливает неинтерактивный запрос авторизации (prompt=none).
     *
     * При живой SSO-сессии провайдер сразу вернет код без показа формы.
     * Иначе callback вернет ошибку interaction_required.
     *
     * @return Запрос с URL для открытия в скрытом WebView и state.
     */
    fun beginSilentSignIn(): JournalAuthRequest = auth.createAuthRequest(silent = true)

    /**
     * Завершает вход: обменивает код авторизации на токен и загружает данные студента.
     *
     * 1. Обменивает code+state на токен доступа.
     * 2. Сохраняет токен в безопасное хранилище.
     * 3. Загружает данные студента (проверка токена заодно) и кэширует их.
     *
     * @param code Код авторизации из callback URL.
     * @param state Значение state из callback URL.
     * @return [Flow] с загруженным объектом [Student].
     * @throws Exception Если обмен кода или загрузка завершились ошибкой (пробрасывается из репозиториев).
     */
    fun completeSignIn(code: String, state: String): Flow<Student> = flow {
        val token = auth.exchangeCode(code, state)
        secure.signIn(token)

        val student = service.loadStudentInfo(token)
        storage.saveStudent(student)

        emit(student)
    }.flowOn(Dispatchers.IO)

    /**
     * Выполняет выход из системы.
     *
     * Очищает локальный кэш и удаляет сохраненный токен.
     */
    suspend fun signOut() {
        storage.clear()
        secure.signOut()
    }
}
