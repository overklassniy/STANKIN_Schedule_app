package com.overklassniy.stankinschedule.schedule.widget.ui.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.ui.components.ScheduleWidgetConfigureScreen
import com.overklassniy.stankinschedule.schedule.widget.ui.glance.ScheduleGlanceWidget
import com.overklassniy.stankinschedule.schedule.widget.ui.glance.WidgetStateKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity конфигурации виджета расписания.
 *
 * Назначение: отображает Compose-экран конфигурации, сохраняет настройки
 * в Glance state и возвращает результат системы AppWidgetManager.
 */
@AndroidEntryPoint
class ScheduleWidgetConfigureActivity : AppCompatActivity() {

    private val viewModel: ScheduleWidgetConfigureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // По умолчанию конфигурирование считается отмененным до явного подтверждения.
        setResult(RESULT_CANCELED)

        // Читаем идентификатор виджета из Intent. INVALID_APPWIDGET_ID сигнализирует об ошибке.
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setContent {
            AppTheme {
                ScheduleWidgetConfigureScreen(
                    appWidgetId = appWidgetId,
                    viewModel = viewModel,
                    onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                    onScheduleWidgetChanged = ::onScheduleWidgetChanged,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    /**
     * Обработка подтверждения настроек виджета.
     *
     * Сохраняет данные в Glance state и запускает обновление виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Настройки виджета.
     */
    private fun onScheduleWidgetChanged(appWidgetId: Int, data: ScheduleWidgetData) {
        Log.d(TAG, "onScheduleWidgetChanged: appWidgetId=$appWidgetId, data=$data")
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val manager = GlanceAppWidgetManager(this@ScheduleWidgetConfigureActivity)
                val glanceId = manager.getGlanceIdBy(appWidgetId)
                Log.d(TAG, "onScheduleWidgetChanged: glanceId=$glanceId")

                updateAppWidgetState(this@ScheduleWidgetConfigureActivity, glanceId) { prefs ->
                    WidgetStateKeys.writeToPrefs(prefs, data)
                    Log.d(TAG, "onScheduleWidgetChanged: prefs written=$prefs")
                }

                Log.d(TAG, "onScheduleWidgetChanged: calling update")
                ScheduleGlanceWidget().update(this@ScheduleWidgetConfigureActivity, glanceId)
                Log.d(TAG, "onScheduleWidgetChanged: update done")

                withContext(Dispatchers.Main) {
                    setResult(
                        RESULT_OK,
                        Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
                    )
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "onScheduleWidgetChanged: error", e)
            }
        }
    }

    private companion object {
        private const val TAG = "WidgetConfigure"
    }
}