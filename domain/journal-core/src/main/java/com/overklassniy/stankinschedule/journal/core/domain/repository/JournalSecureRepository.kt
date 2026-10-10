package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthToken
import java.security.GeneralSecurityException

/**
 * Репозиторий для безопасного хранения и управления токеном доступа студента.
 *
 * Отвечает за шифрование, сохранение и восстановление токена авторизации.
 */
interface JournalSecureRepository {

    /**
     * Выполняет вход (сохранение токена доступа).
     *
     * @param token Токен доступа студента.
     * @throws GeneralSecurityException Если возникла ошибка при шифровании или сохранении.
     */
    @Throws(GeneralSecurityException::class)
    suspend fun signIn(token: JournalAuthToken)

    /**
     * Выполняет выход (удаление токена).
     */
    suspend fun signOut()

    /**
     * Возвращает сохраненный токен доступа.
     *
     * @return Объект [JournalAuthToken].
     * @throws StudentAuthorizedException Если токен не найден, истек или произошла ошибка дешифровки.
     */
    @Throws(StudentAuthorizedException::class)
    suspend fun signCredentials(): JournalAuthToken
}
