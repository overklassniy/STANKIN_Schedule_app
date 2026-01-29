package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import java.security.GeneralSecurityException

/**
 * Репозиторий для безопасного хранения и управления учетными данными студента.
 *
 * Отвечает за шифрование, сохранение и восстановление логина и пароля.
 */
interface JournalSecureRepository {

    /**
     * Выполняет вход (сохранение учетных данных).
     *
     * @param credentials Учетные данные студента.
     * @throws GeneralSecurityException Если возникла ошибка при шифровании или сохранении.
     */
    @Throws(GeneralSecurityException::class)
    suspend fun signIn(credentials: StudentCredentials)

    /**
     * Выполняет выход (удаление учетных данных).
     */
    suspend fun signOut()

    /**
     * Возвращает сохраненные учетные данные.
     *
     * @return Объект [StudentCredentials].
     * @throws StudentAuthorizedException Если данные не найдены или произошла ошибка дешифровки.
     */
    @Throws(StudentAuthorizedException::class)
    suspend fun signCredentials(): StudentCredentials
}