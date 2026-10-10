package com.overklassniy.stankinschedule.core.domain.repository

import android.util.Log

/**
 * Отладочная реализация краш-репортинга, выводящая ошибки в Logcat.
 *
 * Используется как fallback в CompositionLocal, когда реальная реализация
 * недоступна (например, в превью Compose).
 */
class ConsoleCrashReporter : CrashReporter {

    /**
     * Логирует исключение в консоль с уровнем ERROR.
     *
     * @param t Исключение, которое нужно залогировать.
     */
    override fun recordException(t: Throwable) {
        Log.e("CrashReporter", t.toString(), t)
    }
}
