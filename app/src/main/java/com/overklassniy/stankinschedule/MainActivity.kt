package com.overklassniy.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.migration.Migrator
import com.overklassniy.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Главная Activity приложения.
 * Инициализирует UI и выполняет миграцию данных при первом запуске.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var migrator: dagger.Lazy<Migrator>

    @Inject
    lateinit var appPreference: ApplicationPreference

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    /**
     * Инициализирует Activity, устанавливает splash screen и настраивает Compose UI.
     * Выполняет миграцию данных версии 2.0 при первом запуске.
     *
     * @param savedInstanceState Сохраненное состояние Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    MainScreen()
                }
            }
        }

        lifecycleScope.launch {
            if (!appPreference.isMigrate_2_0) {
                withContext(Dispatchers.IO) {
                    migrator.get().migrate_2_0_0()
                }
                appPreference.isMigrate_2_0 = true
            }
        }
    }
}