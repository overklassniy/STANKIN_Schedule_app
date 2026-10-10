package com.overklassniy.stankinschedule.core.domain.repository

import android.util.Log

/**
 * Отладочная реализация продуктовой аналитики, выводящая данные в Logcat.
 *
 * Используется как fallback в CompositionLocal, когда реальная реализация
 * недоступна (например, в превью Compose).
 */
class ConsoleProductAnalytics : ProductAnalytics {

    /**
     * Логирует обычное событие в консоль с уровнем INFO.
     *
     * @param type Тип события (например, просмотр экрана).
     * @param value Значение или описание события.
     */
    override fun logEvent(type: String, value: String) {
        Log.i("ProductAnalytics", "$type: $value")
    }
}
