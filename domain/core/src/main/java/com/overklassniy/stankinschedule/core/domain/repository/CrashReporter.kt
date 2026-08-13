package com.overklassniy.stankinschedule.core.domain.repository

/**
 * Интерфейс для отчётности об ошибках и крашах.
 *
 * Предоставляет абстракцию для отправки исключений в различные сервисы
 * (например, Sentry или просто в консоль).
 */
interface CrashReporter {

    /**
     * Записывает возникшее исключение в сервис отчётности об ошибках.
     *
     * @param t Исключение (Throwable) для записи.
     */
    fun recordException(t: Throwable)
}