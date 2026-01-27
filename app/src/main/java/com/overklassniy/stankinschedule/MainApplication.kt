package com.overklassniy.stankinschedule

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.domain.settings.DarkMode
import com.overklassniy.stankinschedule.core.ui.R
import com.overklassniy.stankinschedule.core.ui.notification.NotificationUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Класс приложения Stankin Schedule.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var applicationPreference: ApplicationPreference

    override fun onCreate() {
        super.onCreate()

        Firebase.analytics.setAnalyticsCollectionEnabled(applicationPreference.isAnalyticsEnabled)

        if (BuildConfig.DEBUG) {
            Firebase.analytics.setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
        }

        updateDarkMode()
        updateAppLanguage()
        setupNotifications()
    }

    /**
     * Возвращает конфигурацию WorkManager для приложения.
     *
     * @return Конфигурация WorkManager с фабрикой воркеров и уровнем логирования
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Обновляет режим темной темы приложения на основе пользовательских настроек.
     */
    private fun updateDarkMode() {
        val mode = when (applicationPreference.currentDarkMode()) {
            DarkMode.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
        }

        if (mode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    /**
     * Обновляет язык приложения на основе пользовательских настроек.
     */
    private fun updateAppLanguage() {
        val language = applicationPreference.currentAppLanguage()
        val localeList = if (language.localeCode.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.localeCode)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * Устанавливает настройки уведомлений для приложения.
     * Создает каналы уведомлений для Android 8.0+.
     */
    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelCommon = NotificationChannel(
                NotificationUtils.CHANNEL_COMMON,
                getString(R.string.notification_common),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelCommon.description = getString(R.string.notification_common_description)
            channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelCommon.enableVibration(true)
            channelCommon.enableLights(true)

            val channelModuleJournal = NotificationChannel(
                NotificationUtils.CHANNEL_MODULE_JOURNAL,
                getString(R.string.notification_mj),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelModuleJournal.description = getString(R.string.notification_mj_description)
            channelModuleJournal.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelModuleJournal.enableVibration(true)
            channelModuleJournal.enableLights(true)

            getSystemService(NotificationManager::class.java)?.let { manager ->
                manager.createNotificationChannel(channelCommon)
                manager.createNotificationChannel(channelModuleJournal)
            }
        }
    }
}