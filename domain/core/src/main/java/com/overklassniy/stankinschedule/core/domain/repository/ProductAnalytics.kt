package com.overklassniy.stankinschedule.core.domain.repository

/**
 * Интерфейс для логирования продуктовых событий аналитики.
 *
 * Предоставляет абстракцию для отправки данных аналитики в различные сервисы
 * (например, Firebase Analytics или просто в консоль).
 */
interface ProductAnalytics {

    /**
     * Логирует событие аналитики.
     *
     * @param type Тип события (категория).
     * @param value Значение события или дополнительная информация.
     */
    fun logEvent(type: String, value: String)

    companion object {
        /** Константа для события входа на экран. */
        const val SCREEN_ENTER = "screen_enter_view"

        /** Константа для события выхода с экрана. */
        const val SCREEN_LEAVE = "screen_leave_view"
    }
}
