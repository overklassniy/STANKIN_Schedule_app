package com.overklassniy.stankinschedule.journal.predict.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.journal.predict.ui.components.PredictScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Активити предсказания рейтинга.
 *
 * Отображает список дисциплин и панель результата, обрабатывает выбор семестра.
 */
@AndroidEntryPoint
class PredictActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: PredictViewModel by viewModels()

    /**
     * Инициализирует экран через Compose [setContent].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                PredictScreen(
                    viewModel = viewModel,
                    onBackPressed = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }

        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_ENTER, "PredictActivity")
    }

    /**
     * Логирует выход с экрана.
     */
    override fun onDestroy() {
        super.onDestroy()
        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, "PredictActivity")
    }
}