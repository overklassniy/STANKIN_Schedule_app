package com.overklassniy.stankinschedule.journal.core.domain.exceptions

/**
 * Исключение, выбрасываемое при ошибках авторизации студента.
 *
 * Указывает на то, что данные учетной записи неверны или сессия истекла.
 */
class StudentAuthorizedException : Exception {

    /**
     * Создает исключение с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    constructor(message: String?) : super(message)

    /**
     * Создает исключение на основе другого исключения (причины).
     *
     * @param cause Причина возникновения исключения.
     */
    constructor(cause: Throwable?) : super(cause)
}