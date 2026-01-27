package com.overklassniy.stankinschedule

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ApplicationInfo
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
 * Класс приложения, инициализирующий глобальные компоненты при запуске.
 *
 * Отвечает за:
 * 1. Инициализацию Hilt (DI).
 * 2. Настройку WorkManager.
 * 3. Инициализацию Firebase Analytics и Crashlytics.
 * 4. Применение настроек темы и языка.
 * 5. Создание каналов уведомлений.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    // Фабрика для создания воркеров с поддержкой Hilt
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Настройки приложения (тема, язык, аналитика)
    @Inject
    lateinit var applicationPreference: ApplicationPreference

    // Проверка, запущено ли приложение в режиме отладки
    private val isDebug: Boolean
        get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Вызывается при запуске приложения.
     *
     * Инициализирует аналитику, тему, язык и уведомления.
     */
    override fun onCreate() {
        super.onCreate()

        // Установка разрешения на сбор аналитики согласно настройкам пользователя
        Firebase.analytics.setAnalyticsCollectionEnabled(applicationPreference.isAnalyticsEnabled)

        // В режиме отладки отключаем аналитику и сбор крашей
        if (isDebug) {
            Firebase.analytics.setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }

        updateDarkMode()
        updateAppLanguage()
        setupNotifications()
    }

    /**
     * Конфигурация WorkManager.
     *
     * Определяет фабрику воркеров и уровень логирования (DEBUG в режиме отладки).
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .apply {
                if (isDebug) {
                    setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Обновляет тему приложения (светлая/темная/системная).
     *
     * Считывает настройки из [applicationPreference] и применяет их через [AppCompatDelegate].
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
     * Обновляет язык приложения.
     *
     * Считывает настройки языка из [applicationPreference] и применяет их глобально.
     * Если язык не выбран, используется системный.
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
     * Настраивает каналы уведомлений.
     *
     * Создает каналы для общих уведомлений и уведомлений модульного журнала.
     * Настраивает важность, видимость на экране блокировки, вибрацию и светодиод.
     */
    private fun setupNotifications() {
        // Канал для общих уведомлений
        val channelCommon = NotificationChannel(
            NotificationUtils.CHANNEL_COMMON,
            getString(R.string.notification_common),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channelCommon.description = getString(R.string.notification_common_description)
        channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channelCommon.enableVibration(true)
        channelCommon.enableLights(true)

        // Канал для уведомлений модульного журнала
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