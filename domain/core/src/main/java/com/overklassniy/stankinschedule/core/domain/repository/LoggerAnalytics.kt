package com.overklassniy.stankinschedule.core.domain.repository

/**
 * Интерфейс для логирования событий аналитики и ошибок.
 *
 * Предоставляет абстракцию для отправки данных аналитики в различные сервисы
 * (например, Firebase Analytics, AppCenter или просто в консоль).
 */
interface LoggerAnalytics {

    /**
     * Логирует событие аналитики.
     *
     * @param type Тип события (категория).
     * @param value Значение события или дополнительная информация.
     */
    fun logEvent(type: String, value: String)

    /**
     * Логирует возникшее исключение.
     *
     * @param t Исключение (Throwable) для записи.
     */
    fun recordException(t: Throwable)

    companion object {
        /** Константа для события входа на экран. */
        const val SCREEN_ENTER = "screen_enter_view"

        /** Константа для события выхода с экрана. */
        const val SCREEN_LEAVE = "screen_leave_view"
    }
}