package com.overklassniy.stankinschedule.schedule.table.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity просмотра таблицы расписания.
 *
 * Назначение: хостит Compose-экран ScheduleTableScreen и передает ему идентификатор
 * расписания через Intent extras.
 */
@AndroidEntryPoint
class ScheduleTableViewActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: ScheduleTableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Отключаем системные отступы, чтобы контент занимал всю область экрана.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Читаем идентификатор расписания из Intent. Значение -1 используется как маркер отсутствия.
        val scheduleId = intent.getLongExtra(SCHEDULE_ID, -1)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    ScheduleTableScreen(
                        scheduleId = scheduleId,
                        viewModel = viewModel,
                        onBackClicked = { onBackPressedDispatcher.onBackPressed() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    companion object {

        /**
         * Ключ для передачи идентификатора расписания в Intent extras.
         * Инвариант: совпадает по обе стороны передачи.
         */
        private const val SCHEDULE_ID = "schedule_id"

        /**
         * Создает Intent для запуска Activity просмотра таблицы.
         *
         * @param context Контекст, из которого создается Intent.
         * @param scheduleId Идентификатор расписания.
         * @return Intent с установленным extra SCHEDULE_ID.
         */
        fun createIntent(context: Context, scheduleId: Long): Intent {
            return Intent(context, ScheduleTableViewActivity::class.java).apply {
                putExtra(SCHEDULE_ID, scheduleId)
            }
        }
    }
}

