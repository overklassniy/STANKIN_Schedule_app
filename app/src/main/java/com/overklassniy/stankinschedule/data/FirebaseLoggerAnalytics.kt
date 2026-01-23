package com.overklassniy.stankinschedule.data

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import javax.inject.Inject

class FirebaseLoggerAnalytics @Inject constructor() : LoggerAnalytics {

    private val analytics = Firebase.analytics
    private val crashlytics = Firebase.crashlytics

    override fun logEvent(type: String, value: String) {
        analytics.logEvent(type) { param("event", value) }
    }

    override fun recordException(t: Throwable) {
        crashlytics.recordException(t)
    }
}