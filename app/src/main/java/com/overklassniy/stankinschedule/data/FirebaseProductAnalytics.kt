package com.overklassniy.stankinschedule.data

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import javax.inject.Inject

/**
 * Реализация ProductAnalytics с использованием Firebase Analytics.
 *
 * Отвечает только за продуктовые события (просмотр экранов, действия пользователя).
 * Краш-репортинг вынесен в [SentryCrashReporter].
 */
class FirebaseProductAnalytics @Inject constructor() : ProductAnalytics {

    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    /**
     * Логирует событие в Firebase Analytics.
     *
     * @param type Тип события.
     * @param value Значение события.
     */
    override fun logEvent(type: String, value: String) {
        analytics.logEvent(type) { param("event", value) }
    }
}