package com.overklassniy.stankinschedule

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration as WorkConfiguration
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.core.ui.R
import com.overklassniy.stankinschedule.core.ui.notification.NotificationUtils
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsWidgetUpdater
import com.overklassniy.stankinschedule.news.widget.ui.worker.NewsWidgetWorkerScheduler
import com.overklassniy.stankinschedule.schedule.widget.ui.glance.ScheduleWidgetUpdater
import dagger.hilt.android.HiltAndroidApp
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Класс приложения, инициализирующий глобальные компоненты при запуске.
 *
 * Отвечает за:
 * 1. Инициализацию Hilt (DI).
 * 2. Настройку WorkManager.
 * 3. Инициализацию Firebase Analytics (продуктовая аналитика) и Sentry (краши/перфоманс).
 * 4. Применение настроек темы и языка.
 * 5. Создание каналов уведомлений.
 */
@HiltAndroidApp
class MainApplication : Application(), WorkConfiguration.Provider {

    // Фабрика для создания воркеров с поддержкой Hilt
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Настройки приложения (тема, язык, аналитика)
    @Inject
    lateinit var applicationPreference: ApplicationPreference

    // Проверка, запущено ли приложение в режиме отладки
    private val isDebug: Boolean
        get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    // Последнее известное состояние ночного режима для отслеживания смены темы
    private var lastNightMode: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

    /**
     * Вызывается при запуске приложения.
     *
     * Инициализирует аналитику, тему, язык и уведомления.
     * Регистрирует колбэк для отслеживания смены системной темы (тёмная/светлая)
     * и обновления виджета при изменении.
     */
    override fun onCreate() {
        super.onCreate()

        lastNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // Инициализация Sentry для краш-репортинга и мониторинга производительности
        SentryAndroid.init(this) { options ->
            options.dsn = BuildConfig.SENTRY_DSN

            // В режиме отладки отключаем отправку событий в Sentry
            if (isDebug) {
                options.setEnabled(false)
            }

            // Фильтруем неактуальные исключения, которые не являются реальными ошибками
            options.beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
                val exceptions = event.exceptions ?: return@BeforeSendCallback event
                for (ex in exceptions) {
                    val type = ex.type ?: continue
                    // CancellationException и его подклассы (включая LeftCompositionCancellationException)
                    // возникают при нормальной отмене корутин в жизненном цикле Compose
                    if (type.endsWith("CancellationException")) {
                        return@BeforeSendCallback null
                    }
                    // DiagnosticCoroutineContextException — диагностическое исключение корутин,
                    // не является реальной ошибкой
                    if (type == "kotlinx.coroutines.DiagnosticCoroutineContextException") {
                        return@BeforeSendCallback null
                    }
                }
                event
            }
        }

        // Запускаем инициализацию настроек асинхронно, так как DataStore работает асинхронно
        CoroutineScope(Dispatchers.Main).launch {
            // Установка разрешения на сбор аналитики согласно настройкам пользователя
            val analyticsEnabled = applicationPreference.isAnalyticsEnabled()
            Firebase.analytics.setAnalyticsCollectionEnabled(analyticsEnabled)

            // Управление Sentry в зависимости от настроек пользователя
            if (!analyticsEnabled) {
                io.sentry.Sentry.close()
            }

            // В режиме отладки отключаем аналитику и Sentry
            if (isDebug) {
                Firebase.analytics.setAnalyticsCollectionEnabled(false)
                io.sentry.Sentry.close()
            }

            updateThemePreset()
            updateAppLanguage()
        }

        setupNotifications()
        registerThemeChangeCallback()
        NewsWidgetWorkerScheduler.schedule(this)
    }

    /**
     * Регистрирует колбэк для отслеживания изменения конфигурации системы.
     *
     * При смене системной темы (тёмная/светлая) обновляет флаг isDarkTheme
     * в Glance state всех экземпляров виджетов и вызывает updateAll.
     * Запись в state триггерит рекомпозицию через currentState, даже если
     * provideGlance ещё активен (updateAll не перезапускает его).
     * Это работает только когда процесс приложения запущен.
     */
    private fun registerThemeChangeCallback() {
        registerComponentCallbacks(@Suppress("OVERRIDE_DEPRECATION") object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                val nightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightMode != lastNightMode) {
                    lastNightMode = nightMode
                    val isDark = nightMode == Configuration.UI_MODE_NIGHT_YES
                    ScheduleWidgetUpdater.updateThemeState(this@MainApplication, isDark)
                    NewsWidgetUpdater.updateThemeState(this@MainApplication, isDark)
                }
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        })
    }

    /**
     * Конфигурация WorkManager.
     *
     * Определяет фабрику воркеров и уровень логирования (DEBUG в режиме отладки).
     */
    override val workManagerConfiguration: WorkConfiguration
        get() = WorkConfiguration.Builder()
            .apply {
                if (isDebug) {
                    setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Обновляет тему приложения на основе сохранённого пресета.
     *
     * Считывает настройки из [applicationPreference] и применяет их через [AppCompatDelegate].
     * Для пресета Custom используется системный режим, так как кастомная тема
     * может быть как светлой, так и тёмной.
     */
    @Suppress("DEPRECATION")
    private suspend fun updateThemePreset() {
        val mode = when (applicationPreference.currentThemePreset()) {
            ThemePreset.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemePreset.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            ThemePreset.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemePreset.Custom -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
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
    private suspend fun updateAppLanguage() {
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