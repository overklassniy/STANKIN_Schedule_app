package com.overklassniy.stankinschedule.data

import io.sentry.Sentry
import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import javax.inject.Inject

/**
 * Реализация CrashReporter с использованием Sentry SDK.
 *
 * Отвечает за отчётность об ошибках и крашах.
 * Продуктовая аналитика вынесена в [FirebaseProductAnalytics].
 */
class SentryCrashReporter @Inject constructor() : CrashReporter {

    /**
     * Записывает исключение в Sentry.
     *
     * @param t Исключение для записи.
     */
    override fun recordException(t: Throwable) {
        Sentry.captureException(t)
    }
}