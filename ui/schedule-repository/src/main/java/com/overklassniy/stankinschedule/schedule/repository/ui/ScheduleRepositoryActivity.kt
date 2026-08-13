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
import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalCrashReporter
import com.overklassniy.stankinschedule.core.ui.components.LocalProductAnalytics
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
     * Продуктовая аналитика, передается в Compose через LocalProductAnalytics.
     */
    @Inject
    lateinit var productAnalytics: ProductAnalytics

    /**
     * Краш-репортинг, передается в Compose через LocalCrashReporter.
     */
    @Inject
    lateinit var crashReporter: CrashReporter

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
                CompositionLocalProvider(
                    LocalProductAnalytics provides productAnalytics,
                    LocalCrashReporter provides crashReporter
                ) {
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