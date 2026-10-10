package com.overklassniy.stankinschedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalCrashReporter
import com.overklassniy.stankinschedule.core.ui.components.LocalProductAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Главная Activity приложения.
 *
 * Является точкой входа в UI приложения.
 * Отвечает за инициализацию темы, настройку системных окон (edge-to-edge)
 * и запуск основного экрана [MainScreen].
 *
 * Аннотация @AndroidEntryPoint указывает Hilt на необходимость внедрения зависимостей.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Сервис продуктовой аналитики, внедряемый через Hilt
    @Inject
    lateinit var productAnalytics: ProductAnalytics

    // Сервис краш-репортинга, внедряемый через Hilt
    @Inject
    lateinit var crashReporter: CrashReporter

    // Текущий Intent для обработки deep-link, обновляется в onNewIntent
    private val currentIntent = mutableStateOf<Intent?>(null)

    /**
     * Метод жизненного цикла Activity, вызываемый при создании.
     *
     * Выполняет следующие задачи:
     * 1. Устанавливает SplashScreen с поддержкой анимации.
     * 2. Настраивает отображение контента на весь экран (edge-to-edge).
     * 3. Устанавливает контент Activity с использованием Jetpack Compose.
     * 4. Оборачивает контент в тему приложения [AppTheme].
     * 5. Предоставляет экземпляры [LocalProductAnalytics] и [LocalCrashReporter] для всех дочерних Composable.
     *
     * @param savedInstanceState Сохраненное состояние Activity (если есть).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // ВАЖНО: installSplashScreen() должен быть вызван ДО super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Держим сплеш-экран пока анимация не завершится (600ms + запас)
        val startTime = System.currentTimeMillis()
        splashScreen.setKeepOnScreenCondition {
            System.currentTimeMillis() - startTime < SPLASH_HOLD_MS
        }

        // Отключение стандартных отступов системных окон для реализации edge-to-edge интерфейса
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Сохраняем стартовый Intent для обработки deep-link
        currentIntent.value = intent

        setContent {
            // Применение темы приложения
            AppTheme {
                // Предоставление аналитики и краш-репортинга через CompositionLocal
                CompositionLocalProvider(
                    LocalProductAnalytics provides productAnalytics,
                    LocalCrashReporter provides crashReporter
                ) {
                    // Отображение основного экрана приложения
                    MainScreen(
                        intent = currentIntent.value,
                        onIntentHandled = { currentIntent.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent.value = intent
    }

    private companion object {

        /** Длительность удержания сплеш-экрана в миллисекундах. */
        const val SPLASH_HOLD_MS = 800L
    }
}
