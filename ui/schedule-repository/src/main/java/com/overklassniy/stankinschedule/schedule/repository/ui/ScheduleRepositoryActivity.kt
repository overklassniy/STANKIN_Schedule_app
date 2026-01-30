package com.overklassniy.stankinschedule.schedule.repository.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Активность репозитория расписаний.
 *
 * Предоставляет контейнер для Compose-экрана и внедряет аналитику.
 */
@AndroidEntryPoint
class ScheduleRepositoryActivity : AppCompatActivity() {

    /**
     * Аналитика событий (LoggerAnalytics), передается в Compose через LocalAnalytics.
     */
    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: ScheduleRepositoryViewModel by viewModels()

    /**
     * Инициализация UI.
     *
     * Алгоритм:
     *  1. Отключает системные отступы (edge-to-edge).
     *  2. Устанавливает тему приложения.
     *  3. Передает аналитику через CompositionLocal и отображает экран.
     *
     * @param savedInstanceState Сохраненное состояние.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    ScheduleRepositoryScreen(
                        onBackPressed = {
                            onBackPressedDispatcher.onBackPressed()
                        },
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}