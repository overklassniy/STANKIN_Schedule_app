package com.overklassniy.stankinschedule.core.domain.repository

import android.util.Log

/**
 * Реализация логгера аналитики, выводящая данные в системную консоль (Logcat).
 *
 * Используется для отладки, выводя события уровня INFO и ошибки уровня ERROR.
 * Тэг логов: "LoggerAnalytics".
 */
class ConsoleLoggerAnalytics : LoggerAnalytics {

    /**
     * Логирует обычное событие в консоль с уровнем INFO.
     *
     * @param type Тип события (например, просмотр экрана).
     * @param value Значение или описание события.
     */
    override fun logEvent(type: String, value: String) {
        Log.i("LoggerAnalytics", "$type: $value")
    }

    /**
     * Логирует исключение в консоль с уровнем ERROR.
     *
     * @param t Исключение, которое нужно залогировать.
     */
    override fun recordException(t: Throwable) {
        Log.e("LoggerAnalytics", t.toString(), t)
    }
}