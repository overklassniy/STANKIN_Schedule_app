package com.overklassniy.stankinschedule.data

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import javax.inject.Inject

/**
 * Реализация LoggerAnalytics с использованием Firebase Analytics и Crashlytics.
 */
class FirebaseLoggerAnalytics @Inject constructor() : LoggerAnalytics {

    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }
    private val crashlytics: FirebaseCrashlytics by lazy { Firebase.crashlytics }

    /**
     * Логирует событие в Firebase Analytics.
     *
     * @param type Тип события
     * @param value Значение события
     */
    override fun logEvent(type: String, value: String) {
        analytics.logEvent(type) { param("event", value) }
    }

    /**
     * Записывает исключение в Firebase Crashlytics.
     *
     * @param t Исключение для записи
     */
    override fun recordException(t: Throwable) {
        crashlytics.recordException(t)
    }
}