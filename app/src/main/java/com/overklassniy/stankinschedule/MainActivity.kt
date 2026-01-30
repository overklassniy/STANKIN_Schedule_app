package com.overklassniy.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
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

    // Сервис аналитики и логирования, внедряемый через Hilt
    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    /**
     * Метод жизненного цикла Activity, вызываемый при создании.
     *
     * Выполняет следующие задачи:
     * 1. Устанавливает SplashScreen с поддержкой анимации.
     * 2. Настраивает отображение контента на весь экран (edge-to-edge).
     * 3. Устанавливает контент Activity с использованием Jetpack Compose.
     * 4. Оборачивает контент в тему приложения [AppTheme].
     * 5. Предоставляет экземпляр аналитики [LocalAnalytics] для всех дочерних Composable.
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
            System.currentTimeMillis() - startTime < 800
        }

        // Отключение стандартных отступов системных окон для реализации edge-to-edge интерфейса
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Применение темы приложения
            AppTheme {
                // Предоставление аналитики через CompositionLocal
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    // Отображение основного экрана приложения
                    MainScreen()
                }
            }
        }
    }
}